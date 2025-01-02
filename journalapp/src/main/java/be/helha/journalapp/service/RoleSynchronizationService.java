package be.helha.journalapp.service;

import be.helha.journalapp.model.Role;
import be.helha.journalapp.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This service is responsible for synchronizing roles between the local database and Keycloak.
 * <p>
 * It periodically fetches roles from Keycloak and updates the local database to reflect any changes or additions.
 * This ensures that the local application always has an up-to-date view of the roles defined in Keycloak.
 */
@Service
public class RoleSynchronizationService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    /**
     * Synchronizes roles between Keycloak and the local database.
     * <p>
     * This method is scheduled to run every hour using a cron expression. It retrieves
     * all roles from Keycloak using {@link KeycloakAdminService#getAllRolesFromKeycloak()}.
     * For each role from Keycloak, it checks if a role with the same name already exists
     * in the local database.
     * If it exists, the method updates the existing role with the Keycloak ID.
     * If it does not exist, the method creates a new role.
     */
    @Scheduled(cron = "0 0 * * * ?") // Exécute toutes les heures
    public void synchronizeRoles() {
        List<Role> keycloakRoles = keycloakAdminService.getAllRolesFromKeycloak();
        for (Role keycloakRole : keycloakRoles) {
            Optional<Role> existingRole = roleRepository.findByRoleName(keycloakRole.getRoleName());
            if (existingRole.isPresent()) {
                Role localRole = existingRole.get();
                localRole.setKeycloakRoleId(keycloakRole.getKeycloakRoleId()); // Utilise l'ID Keycloak
                roleRepository.save(localRole);
            } else {
                Role newRole = new Role();
                newRole.setRoleName(keycloakRole.getRoleName());
                newRole.setKeycloakRoleId(keycloakRole.getKeycloakRoleId());
                roleRepository.save(newRole);
            }
        }
    }
}