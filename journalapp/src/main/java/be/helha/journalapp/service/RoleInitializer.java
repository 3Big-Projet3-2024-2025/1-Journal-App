package be.helha.journalapp.service;

import be.helha.journalapp.model.Role;
import be.helha.journalapp.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Service qui initialise les rôles dans la DB locale,
 * et leur associe le keycloakRoleId.
 */
@Service
public class RoleInitializer {

    private final RoleRepository roleRepository;

    // Map statique : roleName -> keycloakRoleId
    // (les UUID viennent de votre Keycloak, récupérés via l'Admin console ou l'API)
    private static final Map<String, String> ROLE_MAPPING = Map.of(
            "ADMIN",      "a832a520-f320-4e54-9bf4-5569a21c812e",
            "EDITOR",     "a162b767-6d70-4ceb-9871-acfcf58949ed",
            "JOURNALIST", "1f1e9bf8-57f3-4412-a15a-2b9025d9d9ba",
            "READER",     "bef3a1c3-8bb4-4d2d-b02a-6b09369d547d"
    );

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles() {
        // Pour chaque rôle que vous souhaitez avoir par défaut :
        createOrUpdateRole("ADMIN");
        createOrUpdateRole("EDITOR");
        createOrUpdateRole("JOURNALIST");
        createOrUpdateRole("READER");
    }

    /**
     * Crée le rôle 'roleName' en base locale s'il n'existe pas déjà,
     * et renseigne (ou met à jour) le keycloakRoleId.
     */
    private void createOrUpdateRole(String roleName) {
        // On vérifie si le rôle existe déjà
        Optional<Role> existingRoleOpt = roleRepository.findByRoleName(roleName);

        // Récupérer le keycloakRoleId depuis la Map
        String kcId = ROLE_MAPPING.get(roleName);
        if (kcId == null) {
            // Si jamais la Map ne contient pas le roleName, on pourrait lever une exception
            throw new RuntimeException("Pas de keycloakRoleId trouvé pour le rôle : " + roleName);
        }

        if (existingRoleOpt.isPresent()) {
            // Le rôle existe déjà dans la DB
            Role existingRole = existingRoleOpt.get();

            // Si le keycloakRoleId n'est pas encore renseigné (ou différent), on le met à jour
            if (existingRole.getKeycloakRoleId() == null
                    || !existingRole.getKeycloakRoleId().equals(kcId)) {
                existingRole.setKeycloakRoleId(kcId);
                roleRepository.save(existingRole);
            }

        } else {
            // Le rôle n'existe pas, on le crée
            Role newRole = new Role();
            newRole.setRoleName(roleName);
            newRole.setKeycloakRoleId(kcId);
            roleRepository.save(newRole);
        }
    }
}
