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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(PasswordEncoder passwordEncoder, EmailService emailService, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // CREATE: Add a new user
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Email already exists
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Hash the password
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
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

    // UPDATE: Update an existing user's details
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    existingUser.setLongitude(updatedUser.getLongitude());
                    existingUser.setLatitude(updatedUser.getLatitude());
                    existingUser.setAuthorized(updatedUser.isAuthorized());
                    existingUser.setRoleChange(updatedUser.isRoleChange());
                    existingUser.setRole(updatedUser.getRole());
                    User savedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(savedUser);
                })
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

    // RESET PASSWORD: Update password for a user
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestBody String newPassword) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (newPassword == null || newPassword.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password cannot be empty");
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return ResponseEntity.ok("Password reset successfully");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the specified email not found"));
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

    // MARK NEWSLETTER AS READ
    @PatchMapping("/{userId}/mark-newsletter-as-read/{newsletterId}")
    public ResponseEntity<String> markNewsletterAsRead(
            @PathVariable Long userId,
            @PathVariable Long newsletterId) {
        return userRepository.findById(userId)
                .map(user -> {
                    Newsletter newsletter = user.getNewsletters().stream()
                            .filter(n -> n.getNewsletterId().equals(newsletterId))
                            .findFirst()
                            .orElse(null);
                    if (newsletter == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Newsletter not found for this user");
                    }
                    newsletter.setRead(true);
                    userRepository.save(user);
                    return ResponseEntity.ok("Newsletter marked as read successfully");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }




    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        // Check if email already exists
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A user with this email already exists.");
        }

        // Hash password
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // Save the new user
        userRepository.save(newUser);

        try {
            // Send a welcome email to the user
            String subject = "Welcome to JournalApp!";
            String content = "<p>Hello " + newUser.getFirstName() + ",</p>"
                    + "<p>Thank you for registering with JournalApp. We are thrilled to have you onboard!</p>"
                    + "<p>Explore our platform and make the most of it.</p>"
                    + "<p>Best regards,</p>"
                    + "<p>The JournalApp Team</p>";

            emailService.sendEmail(newUser.getEmail(), subject, content);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully and email sent.");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registered but email sending failed.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }






}
