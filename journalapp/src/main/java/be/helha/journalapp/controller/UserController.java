package be.helha.journalapp.controller;

import be.helha.journalapp.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private List<User> users = new ArrayList<>(); // In-memory storage for users
    private Long currentId = 1L; // Counter for generating unique IDs

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
                user.setLastName(updatedUser.getLastName()); // Update last name
                user.setFirstName(updatedUser.getFirstName()); // Update first name
                user.setBirthDate(updatedUser.getBirthDate()); // Update birth date
                user.setEmail(updatedUser.getEmail()); // Update email
                user.setPassword(updatedUser.getPassword()); // Update password
                user.setNewPassword(updatedUser.getNewPassword()); // Update new password
                user.setLongitude(updatedUser.getLongitude()); // Update longitude
                user.setLatitude(updatedUser.getLatitude()); // Update latitude
                user.setAuthorized(updatedUser.isAuthorized()); // Update authorization status
                user.setRoleChanged(updatedUser.isRoleChanged()); // Update role change status
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
                user.setRoleChanged(true); // Mark role as changed
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
                user.setAuthorized(true); // Mark the user as authorized
                return user; // Return the updated user
            }
        }
        return null; // Return null if no user is found
    }
}
