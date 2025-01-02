package be.helha.journalapp.controller;

import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.Role;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.RoleRepository;
import be.helha.journalapp.repositories.UserRepository;
import be.helha.journalapp.service.EmailService;
import be.helha.journalapp.service.KeycloakAdminService;
import be.helha.journalapp.service.RoleSynchronizationService;
import be.helha.journalapp.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing user resources.
 * This controller handles requests related to retrieving, deleting, and modifying user information,
 * as well as handling password reset requests and user authorization.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    private final  KeycloakAdminService keycloakAdminService;
    private final RoleSynchronizationService roleSynchronizationService;

    /**
     * Constructor for UserController, injecting dependencies.
     * @param emailService The service for sending emails.
     * @param userRepository The repository for accessing user data.
     * @param roleRepository The repository for accessing role data.
     */
    public UserController( EmailService emailService, UserRepository userRepository, RoleRepository roleRepository,
                           KeycloakAdminService keycloakAdminService , RoleSynchronizationService roleSynchronizationService,UserService userService) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.roleSynchronizationService = roleSynchronizationService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody Map<String, Object> userDetails) {
        try {
            String userId = userService.createUser(userDetails);
            return ResponseEntity.ok("Utilisateur créé avec l'ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }
    }


    /**
     * Retrieves all users.
     *
     * @return A ResponseEntity containing a list of all User objects.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A ResponseEntity containing the User object if found, or a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return A ResponseEntity with a success message if deleted, or a 404 Not Found response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User userToDelete = user.get();
            // Supprime l'utilisateur dans Keycloak
            keycloakAdminService.deleteUserInKeycloak(userToDelete.getKeycloakId());

            // Supprime l'utilisateur dans la base locale
            userRepository.deleteById(id);

            return ResponseEntity.ok("User deleted successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }




    /**
     * Changes the role of a user by their ID.
     *
     * @param id The ID of the user whose role needs to be changed.
     * @param newRoleName The new role name to assign to the user.
     * @return A ResponseEntity containing the updated User object if successful, or a 404 Not Found response.
     * @throws RuntimeException if the role is not found.
     */
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> changeUserRole(@PathVariable Long id, @RequestBody String newRoleName) {
        return userRepository.findById(id)
                .map(user -> {
                    Role newRole = roleRepository.findByRoleName(newRoleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + newRoleName));
                    user.setRole(newRole);
                    user.setRoleChange(true);
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Authorizes a user by setting the authorized attribute to true.
     * @param id The ID of the user to authorize.
     * @return A ResponseEntity containing the updated User object if found, or a 404 Not Found response.
     */
    @PatchMapping("/{id}/authorize")
    public ResponseEntity<User> authorizeUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setAuthorized(true);
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    /**
     * Updates an existing user's information.
     */
    @PatchMapping("/{id}/update")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userUpdates) {
       //log.info("Updating user with ID: {}", id);
       // log.info("User updates received: {}", userUpdates);

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();

            // Mise à jour des informations dans Keycloak
            keycloakAdminService.updateUserInKeycloak(user.getKeycloakId(), Map.of(
                    "firstName", userUpdates.getFirstName(),
                    "lastName", userUpdates.getLastName(),
                    "email", userUpdates.getEmail()
            ));
          //  log.info("User updated in Keycloak");

            // Vérification de l'ID du rôle
            if (userUpdates.getRole() == null || userUpdates.getRole().getRoleId() == null) {
             //   log.error("Role ID is null");
                return ResponseEntity.badRequest().body(Map.of("error", "Role ID must not be null"));
            }

            // Charger le rôle depuis la base de données locale
            Role role = roleRepository.findById(userUpdates.getRole().getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
          //  log.info("Role found: {}", role);

            // Mise à jour des informations locales
            user.setFirstName(userUpdates.getFirstName());
            user.setLastName(userUpdates.getLastName());
            user.setEmail(userUpdates.getEmail());
            user.setAuthorized(userUpdates.isAuthorized());
            user.setRoleChange(userUpdates.isRoleChange());
            user.setRole(role);
            userRepository.save(user);
         //   log.info("User updated locally");

            // Assigner le rôle dans Keycloak en utilisant keycloakRoleId
            keycloakAdminService.assignRolesToUser(
                    user.getKeycloakId(),
                    List.of(role)
            );
          //  log.info("Role assigned in Keycloak");

            return ResponseEntity.ok(Map.of("message", "User updated successfully."));
        }

      //  log.error("User not found with ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
    }





    /**
     * Retrieves the current user principal from the security context.
     *
     * @param authentication The authentication object, injected by Spring Security.
     * @return A ResponseEntity containing the authentication principal.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }


    /**
     * Retrieves a user by their Keycloak ID.
     * @param keycloakId The Keycloak ID of the user.
     * @return A ResponseEntity containing the User object if found, or a 404 Not Found response.
     */
    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<User> getUserByKeycloakId(@PathVariable String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    /**
     * Retrieves the current user from the security context using their Keycloak ID.
     *
     * @return A ResponseEntity containing the current User object if found, or a 404 Not Found response.
     */
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        // Récupérez l'ID utilisateur Keycloak à partir du contexte de sécurité
        String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();

        // Recherchez l'utilisateur correspondant dans la base de données
        Optional<User> user = userRepository.findByKeycloakId(keycloakId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    /**
     * Adds a GDPR request to a user's list.
     *
     * @param id The ID of the user.
     * @param gdprRequest The GDPR request to add.
     * @return A ResponseEntity containing the updated User object or an error message.
     */
    @PatchMapping("/{id}/gdpr-requests/add")
    public ResponseEntity<User> addGdprRequest(@PathVariable Long id, @RequestBody String gdprRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    if (user.getGdprRequests() == null) {
                        user.setGdprRequests(new ArrayList<>());
                    }
                    user.getGdprRequests().add(gdprRequest);
                    userRepository.save(user);
                    return ResponseEntity.ok(user); // Retour explicite d'un ResponseEntity<User>
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PatchMapping("/{id}/gdpr-requests/remove")
    public ResponseEntity<User> removeGdprRequest(@PathVariable Long id, @RequestBody String gdprRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    if (user.getGdprRequests() != null && user.getGdprRequests().remove(gdprRequest)) {
                        userRepository.save(user);
                        return ResponseEntity.ok(user); // Retour explicite d'un ResponseEntity<User>
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user); // Assurez-vous de retourner User ou null
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



}
