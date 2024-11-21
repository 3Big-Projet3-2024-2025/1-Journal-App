package be.helha.journalapp.controller;

import be.helha.journalapp.model.Role;
import be.helha.journalapp.repositories.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    // Inject the repository via constructor
    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // CREATE: Add a new role
    @PostMapping
    public ResponseEntity<Role> addRole(@RequestBody Role newRole) {
        Role savedRole = roleRepository.save(newRole); // Save the role in the database
        return ResponseEntity.ok(savedRole);
    }

    // READ: Retrieve all roles
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll(); // Fetch all roles
        return ResponseEntity.ok(roles);
    }

    // READ: Retrieve a specific role by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.map(ResponseEntity::ok) // Return the role if found
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // READ: Retrieve a specific role by its name
    @GetMapping("/by-name")
    public ResponseEntity<Role> getRoleByName(@RequestParam String roleName) {
        Optional<Role> role = roleRepository.findByRoleName(roleName);
        return role.map(ResponseEntity::ok) // Return the role if found
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // UPDATE: Update an existing role
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setRole_Name(updatedRole.getRole_Name()); // Update the role name
                    Role savedRole = roleRepository.save(existingRole); // Save the updated role
                    return ResponseEntity.ok(savedRole);
                })
                .orElse(ResponseEntity.notFound().build()); // Return 404 if the role is not found
    }

    // DELETE: Delete a role by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id); // Delete the role
            return ResponseEntity.ok("Role deleted successfully");
        }
        return ResponseEntity.notFound().build(); // Return 404 if the role is not found
    }
}
