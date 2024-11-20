package be.helha.journalapp.controller;
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

    private final PasswordEncoder passwordEncoder; // Inject the PasswordEncoder
    private List<User> users = new ArrayList<>();
    private Long currentId = 1L;
    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
                user.setDate_Of_Birth(updatedUser.getDate_Of_Birth()); // Update birth date
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

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
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



}
