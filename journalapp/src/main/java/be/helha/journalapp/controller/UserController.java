package be.helha.journalapp.controller;

import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.Role;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.RoleRepository;
import be.helha.journalapp.repositories.UserRepository;
import be.helha.journalapp.service.EmailService;
import be.helha.journalapp.service.KeycloakAdminService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    private final  KeycloakAdminService keycloakAdminService;

    /**
     * Constructor for UserController, injecting dependencies.
     * @param emailService The service for sending emails.
     * @param userRepository The repository for accessing user data.
     * @param roleRepository The repository for accessing role data.
     */
    public UserController( EmailService emailService, UserRepository userRepository, RoleRepository roleRepository,KeycloakAdminService keycloakAdminService) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.keycloakAdminService = keycloakAdminService;
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
     * Sends a password reset email to the user with the specified email address.
     * Generates a reset token, constructs the reset link, and sends the email with reset instructions.
     *
   
     * @return A ResponseEntity with a success message if the email is sent or an error message and a corresponding HTTP status.
     */
  /*  @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    try {
                        String resetToken = "reset-token-" + user.getUserId(); // Generate a token
                        String resetLink = "http://localhost:3306/reset-password?token=" + resetToken; // frontend url
                        String subject = "Password Reset Request";
                        String content = "<p>Hello " + user.getFirstName() + ",</p>"
                                + "<p>You requested a password reset. Click the link below to reset your password:</p>"
                                + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                                + "<p>If you did not request this, please ignore this email.</p>";

                        emailService.sendEmail(user.getEmail(), subject, content);
                        return ResponseEntity.ok("Password reset email sent");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to send password reset email");
                    }
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the specified email not found"));
    }*/

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User newUser) {
        // Prépare les détails pour Keycloak
        Map<String, Object> keycloakUserDetails = Map.of(
                "username", newUser.getEmail(),
                "firstName", newUser.getFirstName(),
                "lastName", newUser.getLastName(),
                "email", newUser.getEmail(),
                "enabled", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", "temporaryPassword",
                        "temporary", true
                ))
        );

        // Créer l'utilisateur dans Keycloak
        String keycloakId = keycloakAdminService.addUserToKeycloak(keycloakUserDetails);

        // Ajouter l'utilisateur dans la base locale
        newUser.setKeycloakId(keycloakId);
        User savedUser = userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
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


    @PatchMapping("/{id}/update")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User userUpdates
    ) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            keycloakAdminService.updateUserInKeycloak(user.getKeycloakId(), Map.of(
                    "firstName", userUpdates.getFirstName(),
                    "lastName", userUpdates.getLastName(),
                    "email", userUpdates.getEmail()
            ));
            user.setFirstName(userUpdates.getFirstName());
            user.setLastName(userUpdates.getLastName());
            user.setEmail(userUpdates.getEmail());
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
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





}
