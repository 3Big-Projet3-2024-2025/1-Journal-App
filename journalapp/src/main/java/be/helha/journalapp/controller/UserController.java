package be.helha.journalapp.controller;

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
    private final KeycloakAdminService keycloakAdminService;
    private final RoleSynchronizationService roleSynchronizationService;

    public UserController(EmailService emailService,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          KeycloakAdminService keycloakAdminService,
                          RoleSynchronizationService roleSynchronizationService,
                          UserService userService) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.roleSynchronizationService = roleSynchronizationService;
        this.userService = userService;
    }

    /**
     * Creates a new user in Keycloak and, if it's the first user, assigns the ADMIN role.
     */
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody Map<String, Object> userDetails) {
        try {
            // On délègue au service la création dans Keycloak + l'assignation de rôle si besoin
            String userId = userService.createUser(userDetails);
            return ResponseEntity.ok("Utilisateur créé avec l'ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }
    }

    /**
     * Retrieves all users from the local DB.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a specific user by their ID (local DB).
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Deletes a user in both the local DB and Keycloak.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User userToDelete = userOpt.get();

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
     * - Met à jour le rôle dans la DB
     * - Synchronise avec Keycloak (supprime l'ancien, ajoute le nouveau)
     */
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> changeUserRole(@PathVariable Long id, @RequestBody String newRoleName) {
        return userRepository.findById(id).map(user -> {
            Role newRole = roleRepository.findByRoleName(newRoleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + newRoleName));

            // Mise à jour en base locale
            user.setRole(newRole);
            user.setRoleChange(true);  // Indicateur éventuel dans votre logique
            User updatedUser = userRepository.save(user);

            // Assigner le nouveau rôle dans Keycloak
            keycloakAdminService.assignRolesToUser(
                    user.getKeycloakId(),
                    List.of(newRole) // On passe la liste unique pour cet utilisateur
            );

            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    /**
     * Authorizes a user by setting the authorized attribute to true in the local DB.
     * (No direct Keycloak action here, sauf si vous vouliez en faire quelque chose.)
     */
    @PatchMapping("/{id}/authorize")
    public ResponseEntity<User> authorizeUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            user.setAuthorized(true);
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Updates an existing user's information (local DB + Keycloak).
     * - Met à jour nom/prénom/email
     * - Met à jour le rôle (supprime l'ancien, ajoute le nouveau)
     */
    @PatchMapping("/{id}/update")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userUpdates) {
        Optional<User> existingUserOpt = userRepository.findById(id);

        if (existingUserOpt.isPresent()) {
            User user = existingUserOpt.get();

            // --- 1) Mise à jour Keycloak (nom, prénom, email)
            keycloakAdminService.updateUserInKeycloak(
                    user.getKeycloakId(),
                    Map.of(
                            "firstName", userUpdates.getFirstName(),
                            "lastName",  userUpdates.getLastName(),
                            "email",     userUpdates.getEmail()
                    )
            );

            // --- 2) Mise à jour du rôle en base locale
            if (userUpdates.getRole() == null || userUpdates.getRole().getRoleId() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Role ID must not be null")
                );
            }

            Role role = roleRepository.findById(userUpdates.getRole().getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found (ID: "
                            + userUpdates.getRole().getRoleId() + ")"));

            // Mise à jour des champs
            user.setFirstName(userUpdates.getFirstName());
            user.setLastName(userUpdates.getLastName());
            user.setEmail(userUpdates.getEmail());
            user.setAuthorized(userUpdates.isAuthorized());
            user.setRoleChange(userUpdates.isRoleChange());
            user.setRole(role);
            userRepository.save(user);

            // --- 3) Mise à jour Keycloak (assignation du nouveau rôle)
            keycloakAdminService.assignRolesToUser(user.getKeycloakId(), List.of(role));

            return ResponseEntity.ok(Map.of("message", "User updated successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found."));
        }
    }

    /**
     * Retrieves a user by their Keycloak ID (local DB).
     */
    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<User> getUserByKeycloakId(@PathVariable String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Retrieves the current user from the security context using their Keycloak ID.
     */
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        String keycloakId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOpt = userRepository.findByKeycloakId(keycloakId);
        return userOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        String keycloakId = authentication.getName();
        Optional<User> maybeLocalUser = userRepository.findByKeycloakId(keycloakId);

        if (maybeLocalUser.isPresent()) {
            User localUser = maybeLocalUser.get();

            // 1) Récupère la liste des rôles Keycloak
            List<String> rolesFromKeycloak = keycloakAdminService.getRealmLevelRoles(keycloakId);

            // 2) Si le user a "ADMIN" dans Keycloak mais pas en local => on met à jour en local
            if (rolesFromKeycloak.contains("ADMIN") &&
                    !"ADMIN".equalsIgnoreCase(localUser.getRole().getRoleName()))
            {
                Role adminRole = roleRepository.findByRoleName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("ADMIN role not found in DB"));
                localUser.setRole(adminRole);
                userRepository.save(localUser);
            }

            return ResponseEntity.ok(localUser);

        } else {
            // Cas où l’utilisateur n’existe pas encore dans la DB locale

            // a) Créer un nouvel enregistrement local
            User newLocalUser = new User();
            newLocalUser.setKeycloakId(keycloakId);
            newLocalUser.setEmail(authentication.getName()); // ou extraire l'email depuis le token
            newLocalUser.setAuthorized(true); // ou false, selon ta logique

            // Rôle par défaut (par ex. READER)
            Role defaultRole = roleRepository.findByRoleName("READER")
                    .orElseThrow(() -> new RuntimeException("READER role not found in DB"));
            newLocalUser.setRole(defaultRole);

            newLocalUser = userRepository.save(newLocalUser);

            // b) Récupérer les rôles Keycloak, vérifier si "ADMIN" est présent
            List<String> rolesFromKeycloak = keycloakAdminService.getRealmLevelRoles(keycloakId);

            if (rolesFromKeycloak.contains("ADMIN")) {
                Role adminRole = roleRepository.findByRoleName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("ADMIN role not found in DB"));
                newLocalUser.setRole(adminRole);
                newLocalUser = userRepository.save(newLocalUser);
            }

            // c) Retourner le nouvel utilisateur local synchronisé
            return ResponseEntity.ok(newLocalUser);
        }
    }



    /**
     * Adds a GDPR request to a user's list (local DB).
     */
    @PatchMapping("/{id}/gdpr-requests/add")
    public ResponseEntity<User> addGdprRequest(@PathVariable Long id, @RequestBody String gdprRequest) {
        return userRepository.findById(id).map(user -> {
            if (user.getGdprRequests() == null) {
                user.setGdprRequests(new ArrayList<>());
            }
            user.getGdprRequests().add(gdprRequest);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Removes a GDPR request from a user's list (local DB).
     */
    @PatchMapping("/{id}/gdpr-requests/remove")
    public ResponseEntity<User> removeGdprRequest(@PathVariable Long id, @RequestBody String gdprRequest) {
        return userRepository.findById(id).map(user -> {
            if (user.getGdprRequests() != null && user.getGdprRequests().remove(gdprRequest)) {
                userRepository.save(user);
                return ResponseEntity.ok(user);
            }
            // Soit on retourne un code 400 car la requête n’existait pas
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Endpoint to fetch users with GDPR requests.
     */
    @GetMapping("/with-gdpr-requests")
    public ResponseEntity<List<User>> getUsersWithGdprRequests() {
        List<User> usersWithGdprRequests = userRepository.findUsersWithGdprRequests();
        if (usersWithGdprRequests.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usersWithGdprRequests);
    }
}
