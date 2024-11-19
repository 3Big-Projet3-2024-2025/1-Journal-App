package be.helha.journalapp.controller;

import be.helha.journalapp.model.Role;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private List<Role> roles = new ArrayList<>(); // In-memory storage for roles
    private Long currentId = 1L; // Counter for generating unique IDs

    // CREATE: Add a new role
    @PostMapping
    public Role addRole(@RequestBody Role newRole) {
        newRole.setRoleId(currentId++); // Set a unique ID for the new role
        roles.add(newRole); // Add the role to the list
        return newRole; // Return the created role
    }

    // READ: Retrieve all roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roles; // Return the list of roles
    }

    // READ: Retrieve a specific role by its ID
    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roles.stream()
                .filter(role -> role.getRoleId().equals(id)) // Find the role with the matching ID
                .findFirst()
                .orElse(null); // Return null if no role is found
    }

    // UPDATE: Update an existing role
    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        for (Role role : roles) {
            if (role.getRoleId().equals(id)) { // Check if the ID matches
                role.setRoleName(updatedRole.getRoleName()); // Update the role name
                return role; // Return the updated role
            }
        }
        return null; // Return null if no role is found
    }

    // DELETE: Delete a role by its ID
    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {
        boolean removed = roles.removeIf(role -> role.getRoleId().equals(id)); // Remove the role
        return removed ? "Role deleted successfully" : "Role not found"; // Return status message
    }
}
