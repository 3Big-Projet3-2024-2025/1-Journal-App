package be.helha.journalapp.controller;

import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.Role;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.RoleRepository;
import be.helha.journalapp.repositories.UserRepository;
import be.helha.journalapp.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController( EmailService emailService, UserRepository userRepository, RoleRepository roleRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }



    // READ: Retrieve all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // READ: Retrieve a specific user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }



    // DELETE: Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }



    // Forgot Password: Send email with reset link
    @PostMapping("/forgot-password")
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
    }

    // Change User Role
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

    // AUTHORIZE USER: Mark a user as authorized
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








    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }


    // READ: Retrieve a specific user by Keycloak ID
    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<User> getUserByKeycloakId(@PathVariable String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


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
