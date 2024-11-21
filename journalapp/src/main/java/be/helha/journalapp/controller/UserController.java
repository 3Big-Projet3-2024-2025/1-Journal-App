package be.helha.journalapp.controller;
import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;

import be.helha.journalapp.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final EmailService emailService; // Declare EmailService
    private final PasswordEncoder passwordEncoder; // Inject the PasswordEncoder
    private List<User> users = new ArrayList<>();
    private Long currentId = 1L;


    public UserController(PasswordEncoder passwordEncoder, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService; // Create EmailService instance
    }

    // CREATE: Add a new user
    @PostMapping
    public User addUser(@RequestBody User newUser) {
        newUser.setUserId(currentId++); // Set a unique ID for the new user
        users.add(newUser); // Add the user to the list
        return newUser; // Return the created user
    }

    // READ: Retrieve all users
    @GetMapping
    public List<User> getAllUsers() {
        return users; // Return the list of users
    }

    // READ: Retrieve a specific user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return users.stream()
                .filter(user -> user.getUserId().equals(id)) // Find the user with the matching ID
                .findFirst()
                .orElse(null); // Return null if no user is found
    }

    // UPDATE: Update an existing user's details
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        for (User user : users) {
            if (user.getUserId().equals(id)) { // Check if the ID matches
                user.setLast_Name(updatedUser.getLast_Name()); // Update last name
                user.setFirst_Name(updatedUser.getFirst_Name()); // Update first name
                user.setDate_Of_Birth(updatedUser.getDate_Of_Birth()); // Update birthdate
                user.setEmail(updatedUser.getEmail()); // Update email
                user.setPassword(updatedUser.getPassword()); // Update password
                user.setNew_Password(updatedUser.getNew_Password()); // Update new password
                user.setLongitude(updatedUser.getLongitude()); // Update longitude
                user.setLatitude(updatedUser.getLatitude()); // Update latitude
                user.setIs_Authorized(updatedUser.isIs_Authorized()); // Update authorization status
                user.setIs_Role_Change(updatedUser.isIs_Role_Change()); // Update role change status
                user.setRole(updatedUser.getRole()); // Update role
                return user; // Return the updated user
            }
        }
        return null; // Return null if no user is found
    }

    // DELETE: Delete a user by ID
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        boolean removed = users.removeIf(user -> user.getUserId().equals(id)); // Remove the user
        return removed ? "User deleted successfully" : "User not found"; // Return status message
    }

    // Additional: Change user role
    @PatchMapping("/{id}/role")
    public User changeUserRole(@PathVariable Long id, @RequestBody User updatedUser) {
        for (User user : users) {
            if (user.getUserId().equals(id)) { // Check if the ID matches
                user.setRole(updatedUser.getRole()); // Update the user's role
                user.setIs_Role_Change(true); // Mark role as changed
                return user; // Return the updated user
            }
        }
        return null; // Return null if no user is found
    }

    // Additional: Authorize a user
    @PatchMapping("/{id}/authorize")
    public User authorizeUser(@PathVariable Long id) {
        for (User user : users) {
            if (user.getUserId().equals(id)) { // Check if the ID matches
                user.setIs_Authorized(true); // Mark the user as authorized
                return user; // Return the updated user
            }
        }
        return null; // Return null if no user is found
    }




    /**
     * Registers a new user in the system.
     *
     * @param newUser The user object containing the details of the new user.
     * @return A ResponseEntity containing a success or error message.
     */
    // NEW: Register a new user (inscription)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User newUser) {
        // Step 1: Validate input
        if (newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }
        if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required");
        }
        if (newUser.getFirst_Name() == null || newUser.getLast_Name() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First name and last name are required");
        }

        // Step 2: Check if the email is already used
        boolean emailExists = users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(newUser.getEmail()));
        if (emailExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        // Step 3: Hash the password before saving
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // Step 4: Assign a unique ID and add the user to the list
        newUser.setUserId(currentId++);
        users.add(newUser);

        try {
            // Step 5: Send welcome email
            String subject = "Welcome to JournalApp";
            String content = "<p>Hello " + newUser.getFirst_Name() + ",</p>"
                    + "<p>Thank you for registering with JournalApp. We are excited to have you on board!</p>"
                    + "<p>Feel free to explore our platform and make the most out of it.</p>"
                    + "<p>Best regards,</p>"
                    + "<p>The JournalApp Team</p>";

            emailService.sendEmail(newUser.getEmail(), subject, content);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully and confirmation email sent");
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registered but failed to send confirmation email");
        }
    }





    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestBody String newPassword) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                if (newPassword == null || newPassword.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password cannot be empty");
                }

                // Step 1: Hash the new password
                String hashedPassword = passwordEncoder.encode(newPassword);

                // Step 2: Update the password in the user object
                user.setPassword(hashedPassword);

                // Step 3: Clear the `new_password` field after applying the change
                user.setNew_Password("");

                return ResponseEntity.ok("Password reset successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the specified email not found");
    }


    /**
     * Sends a password reset email to the user identified by their email address.
     *
     * @param email The email address of the user requesting the password reset.
     * @return A ResponseEntity containing a success or error message.
     */
    // Forgot Password: Send email with reset link
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                try {
                    // Generate a password reset token (simple example)
                    String resetToken = "reset-token-" + user.getUserId();

                    // Generate the reset link
                    String resetLink = "http://localhost:3306/reset-password?token=" + resetToken; // change with frontend url be careful

                    // Email content
                    String subject = "Password Reset Request";
                    String content = "<p>Hello " + user.getFirst_Name() + ",</p>"
                            + "<p>You requested a password reset. Click the link below to reset your password:</p>"
                            + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                            + "<p>If you did not request this, please ignore this email.</p>";

                    // Send the email
                    emailService.sendEmail(user.getEmail(), subject, content);

                    return ResponseEntity.ok("Password reset email sent");
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to send password reset email");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with the specified email not found");
    }



    /**
     * Marks a specific newsletter as read for the user.
     *
     * @param userId       The ID of the user who wants to mark the newsletter as read.
     * @param newsletterId The ID of the newsletter to be marked as read.
     * @return A ResponseEntity containing a success or error message.
     */
    @PatchMapping("/{userId}/mark-newsletter-as-read/{newsletterId}")
    public ResponseEntity<String> markNewsletterAsRead(
            @PathVariable Long userId,
            @PathVariable Long newsletterId) {
        // found user
        User user = users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // found the newsletter
        Newsletter newsletter = user.getNewsletters().stream()
                .filter(n -> n.getNewsletter_Id().equals(newsletterId))
                .findFirst()
                .orElse(null);

        if (newsletter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Newsletter not found for this user");
        }

        // update the newsletter read state
        newsletter.setIsRead(true);

        return ResponseEntity.ok("Newsletter marked as read successfully");
    }





}
