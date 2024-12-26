package be.helha.journalapp.controller;

import be.helha.journalapp.model.Role;
import be.helha.journalapp.repositories.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing user roles.
 * This controller handles requests related to adding, retrieving, updating, and deleting roles.
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    /**
     * Constructor for RoleController, injecting dependencies.
     * @param roleRepository The repository for accessing role data.
     */
    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Creates a new role.
     *
     * @param newRole The Role object to be created.
     * @return A ResponseEntity containing the saved Role object.
     */
    @PostMapping
    public ResponseEntity<Role> addRole(@RequestBody Role newRole) {
        Role savedRole = roleRepository.save(newRole);
        return ResponseEntity.ok(savedRole);
    }

    /**
     * Retrieves all roles.
     *
     * @return A ResponseEntity containing a list of all Role objects.
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id The ID of the role to retrieve.
     * @return A ResponseEntity containing the Role object if found, or a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Retrieves a role by its name.
     *
     * @param roleName The name of the role to retrieve.
     * @return A ResponseEntity containing the Role object if found, or a 404 Not Found response.
     */
    @GetMapping("/by-name")
    public ResponseEntity<Role> getRoleByName(@RequestParam String roleName) {
        Optional<Role> role = roleRepository.findByRoleName(roleName);
        return role.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Updates an existing role by its ID.
     *
     * @param id The ID of the role to update.
     * @param updatedRole The updated Role object.
     * @return A ResponseEntity containing the updated Role object if found, or a 404 Not Found response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setRoleName(updatedRole.getRoleName());
                    Role savedRole = roleRepository.save(existingRole);
                    return ResponseEntity.ok(savedRole);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Deletes a role by its ID.
     *
     * @param id The ID of the role to delete.
     * @return A ResponseEntity with a success message if deleted, or a 404 Not Found response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            return ResponseEntity.ok("Role deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}