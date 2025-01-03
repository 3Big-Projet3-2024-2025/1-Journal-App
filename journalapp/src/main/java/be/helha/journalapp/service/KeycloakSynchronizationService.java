package be.helha.journalapp.service;

import be.helha.journalapp.model.Role;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.RoleRepository;
import be.helha.journalapp.repositories.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KeycloakSynchronizationService {

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Synchronise le/les rôle(s) d'un utilisateur depuis Keycloak vers la DB locale.
     *
     * @param keycloakUserId L'ID Keycloak de l'utilisateur (subject).
     * @return true si la synchro s'est bien passée, false sinon
     */
    public boolean syncUserRolesFromKeycloak(String keycloakUserId) {
        try {
            // 1) Récupère l'instance Keycloak depuis le KeycloakAdminService
            Keycloak keycloak = keycloakAdminService.getKeycloak();

            // 2) Récupère les rôles Keycloak de l'utilisateur
            RealmResource realmResource = keycloak.realm("journalapp");
            RoleMappingResource roleMappingResource = realmResource.users()
                    .get(keycloakUserId)
                    .roles();
            List<RoleRepresentation> realmRoles = roleMappingResource
                    .realmLevel()
                    .listAll();

            if (realmRoles == null || realmRoles.isEmpty()) {
                System.out.println("Utilisateur sans rôle Keycloak ou inexistant.");
                return false;
            }

            // 3) Déterminer le rôle principal (le plus prioritaire)
            String mainRoleName = determineMainRoleName(realmRoles);

            // 4) Trouver l'utilisateur en DB locale
            Optional<User> optionalUser = userRepository.findByKeycloakId(keycloakUserId);
            if (optionalUser.isEmpty()) {
                System.out.println("Aucun utilisateur local trouvé pour keycloakId=" + keycloakUserId);
                return false;
            }
            User user = optionalUser.get();

            // 5) Trouver en DB le rôle correspondant
            Optional<Role> optRole = roleRepository.findByRoleName(mainRoleName);
            if (optRole.isEmpty()) {
                System.out.println("Le rôle " + mainRoleName + " n'existe pas en DB locale.");
                return false;
            }
            Role localRole = optRole.get();

            // 6) Mettre à jour le user local
            user.setRole(localRole);
            userRepository.save(user);

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la synchronisation des rôles Keycloak vers DB : " + e.getMessage());
            return false;
        }
    }

    /**
     * Détermine la stratégie de sélection du rôle principal pour un utilisateur
     * ayant potentiellement plusieurs rôles Keycloak.
     *
     * @param realmRoles Liste de RoleRepresentation
     * @return Nom du rôle principal (ex: "ADMIN", "EDITOR", "READER"...)
     */
    private String determineMainRoleName(List<RoleRepresentation> realmRoles) {
        // Exemple simpliste : priorités ADMIN > EDITOR > JOURNALIST > READER
        List<String> priorityList = List.of("ADMIN", "EDITOR", "JOURNALIST", "READER");

        for (String roleName : priorityList) {
            if (realmRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(roleName))) {
                return roleName;
            }
        }
        // Si aucun de ces rôles, prendre le 1er
        return realmRoles.get(0).getName();
    }
}
