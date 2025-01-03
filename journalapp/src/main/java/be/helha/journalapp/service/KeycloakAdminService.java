package be.helha.journalapp.service;
import org.keycloak.admin.client.resource.RoleScopeResource;


import be.helha.journalapp.model.Role;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;

import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This service provides methods for managing users and roles in Keycloak.
 * It handles interactions with the Keycloak Admin API, including creating, updating,
 * deleting users, and assigning roles.
 */
@Service
public class KeycloakAdminService {

    private Keycloak keycloak;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.username}")
    private String username;

    @Value("${keycloak.password}")
    private String password;


    /**
     * Initializes the Keycloak client using the provided configuration properties.
     * This method is called after the service bean is constructed, ensuring that
     * the Keycloak client is ready for use.
     */
    @PostConstruct
    public void init() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .grantType("password")
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }

    /**
     * Permet aux autres services de récupérer l'instance Keycloak.
     */
    public Keycloak getKeycloak() {
        return this.keycloak;
    }

    /**
     * Retourne l'ID du premier utilisateur de la liste Keycloak (index 0).
     * Utile pour savoir qui est potentiellement promu en ADMIN.
     */
    public String getFirstUserId() {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        // On prend la liste des utilisateurs (limite 1)
        List<UserRepresentation> users = usersResource.list(0, 1);
        if (!users.isEmpty()) {
            return users.get(0).getId(); // ID Keycloak
        }
        return null;
    }




    /**
     * Adds a user to Keycloak and retrieves their Keycloak ID.
     *
     * @param userDetails A map containing the details of the user to be created (username, firstName, lastName, email).
     * @return The Keycloak ID of the newly created user.
     * @throws RuntimeException If an error occurs during the creation process.
     */
    public String addUserToKeycloak(Map<String, Object> userDetails) {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = new UserRepresentation();
            user.setUsername((String) userDetails.get("username"));
            user.setFirstName((String) userDetails.get("firstName"));
            user.setLastName((String) userDetails.get("lastName"));
            user.setEmail((String) userDetails.get("email"));
            user.setEnabled(true);

            Response response = usersResource.create(user);
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatus());
            }

            String location = response.getHeaderString("Location");
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
            throw new RuntimeException("Failed to retrieve user ID from Keycloak response");
        } catch (Exception e) {
            throw new RuntimeException("Error creating user in Keycloak", e);
        }
    }

    /**
     * Updates a user in Keycloak.
     *
     * @param keycloakId The Keycloak ID of the user to update.
     * @param userUpdates A map containing the details to be updated (firstName, lastName, email).
     * @throws RuntimeException If an error occurs during the update process.
     */
    public void updateUserInKeycloak(String keycloakId, Map<String, Object> userUpdates) {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            UsersResource usersResource = realmResource.users();
            UserRepresentation user = usersResource.get(keycloakId).toRepresentation();

            if (userUpdates.containsKey("firstName")) {
                user.setFirstName((String) userUpdates.get("firstName"));
            }
            if (userUpdates.containsKey("lastName")) {
                user.setLastName((String) userUpdates.get("lastName"));
            }
            if (userUpdates.containsKey("email")) {
                user.setEmail((String) userUpdates.get("email"));
            }

            usersResource.get(keycloakId).update(user);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user in Keycloak", e);
        }
    }

    /**
     * Assigne de nouveaux rôles à un utilisateur **sans** retirer les anciens rôles qu'il possède déjà.
     * Si un rôle existe déjà, il n'est pas réajouté (on évite les doublons).
     */
    public void assignRolesToUser(String userId, List<Role> roles) {
        try {
            // 1) Récupère le RealmResource correspondant à votre realm "journalapp"
            RealmResource realmResource = keycloak.realm("journalapp");

            // 2) Récupère l'interface RoleMappingResource pour l’utilisateur
            RoleMappingResource roleMappingResource = realmResource.users().get(userId).roles();

            // 3) Récupère l'interface RoleScopeResource (le scope "realm level")
            RoleScopeResource realmLevelResource = roleMappingResource.realmLevel();

            // 4) Récupérer la liste des rôles existants déjà assignés à l'utilisateur
            List<RoleRepresentation> existingRoles = realmLevelResource.listAll();

            // 5) Construire la liste des nouveaux rôles à partir des entités "Role" (de votre DB locale)
            List<RoleRepresentation> newRoles = roles.stream()
                    .map(r -> {
                        RoleRepresentation rr = new RoleRepresentation();
                        rr.setName(r.getRoleName());
                        rr.setId(r.getKeycloakRoleId());
                        return rr;
                    })
                    .collect(Collectors.toList());

            // 6) Filtrer les rôles pour n'ajouter que ceux qui ne sont pas déjà présents
            List<RoleRepresentation> rolesToAdd = newRoles.stream()
                    .filter(newRole -> existingRoles.stream()
                            .noneMatch(existingRole -> existingRole.getName().equals(newRole.getName())))
                    .collect(Collectors.toList());

            // 7) Ajouter les nouveaux rôles qui n'existaient pas encore
            if (!rolesToAdd.isEmpty()) {
                realmLevelResource.add(rolesToAdd);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error assigning roles to user in Keycloak", e);
        }
    }



    /**
     * Retrieves all roles from the 'journalapp' realm in Keycloak.
     *
     * @return A list of all roles in Keycloak.
     * @throws RuntimeException If an error occurs while fetching roles from Keycloak.
     */
    public List<Role> getAllRolesFromKeycloak() {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            RolesResource rolesResource = realmResource.roles();

            List<RoleRepresentation> roles = rolesResource.list();

            return roles.stream()
                    .map(rr -> new Role(rr.getName(), rr.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching roles from Keycloak", e);
        }
    }

    /**
     * Deletes a user in Keycloak.
     *
     * @param keycloakId The Keycloak ID of the user to delete.
     * @throws RuntimeException If an error occurs during the deletion process.
     */
    public void deleteUserInKeycloak(String keycloakId) {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            UsersResource usersResource = realmResource.users();
            usersResource.get(keycloakId).remove();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user in Keycloak", e);
        }
    }

    /**
     * Assigns the 'ADMIN' role to a user in Keycloak.
     *
     * @param userId The Keycloak ID of the user.
     * @throws RuntimeException If the 'ADMIN' role does not exist in Keycloak or if any other error occurs.
     */
    public void assignAdminToUser(String userId) {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            RoleRepresentation adminRole = realmResource.roles().get("ADMIN").toRepresentation();
            if (adminRole == null) {
                throw new RuntimeException("Le rôle ADMIN n'existe pas dans Keycloak.");
            }

            RoleRepresentation role = new RoleRepresentation();
            role.setName(adminRole.getName());
            role.setId(adminRole.getId());

            realmResource.users().get(userId).roles().realmLevel().add(List.of(role));
            System.out.println("Rôle ADMIN attribué à l'utilisateur avec l'ID: " + userId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'assignation du rôle ADMIN: ", e);
        }
    }

    /**
     * Retrieves the total number of users in the 'journalapp' realm.
     *
     * @return The total user count.
     * @throws RuntimeException If an error occurs while fetching the user count from Keycloak.
     */
    public long getUserCount() {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            UsersResource usersResource = realmResource.users();
            return usersResource.count();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user count from Keycloak", e);
        }
    }

    /**
     * Checks if any user in the 'journalapp' realm has the 'ADMIN' role.
     *
     * @return True if at least one user has the 'ADMIN' role, false otherwise.
     * @throws RuntimeException If an error occurs while checking for the 'ADMIN' role.
     */
    public boolean anyUserHasAdminRole() {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            UsersResource usersResource = realmResource.users();

            List<UserRepresentation> users = usersResource.list();

            for (UserRepresentation user : users) {
                List<RoleRepresentation> roles = usersResource.get(user.getId()).roles().realmLevel().listAll();
                for (RoleRepresentation role : roles) {
                    if ("ADMIN".equalsIgnoreCase(role.getName())) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error checking if any user has ADMIN role", e);
        }
    }

    /**
     * Assigns the 'ADMIN' role to the first user registered in the 'journalapp' realm.
     *
     * @throws RuntimeException If no users are found or if the 'ADMIN' role does not exist in Keycloak.
     */
    public void assignAdminToFirstUser() {
        try {
            RealmResource realmResource = keycloak.realm("journalapp");
            UsersResource usersResource = realmResource.users();

            // Récupérer le premier utilisateur inscrit
            List<UserRepresentation> users = usersResource.list(0, 1);
            if (users.isEmpty()) {
                throw new RuntimeException("No users found in Keycloak to assign ADMIN role.");
            }

            UserRepresentation firstUser = users.get(0);
            String userId = firstUser.getId();

            // Récupérer le rôle ADMIN
            RoleRepresentation adminRole = realmResource.roles().get("ADMIN").toRepresentation();
            if (adminRole == null) {
                throw new RuntimeException("ADMIN role does not exist in Keycloak.");
            }

            // Assigner le rôle ADMIN à l'utilisateur
            RoleRepresentation role = new RoleRepresentation();
            role.setName(adminRole.getName());
            role.setId(adminRole.getId());

            realmResource.users().get(userId).roles().realmLevel().add(List.of(role));
        } catch (Exception e) {
            throw new RuntimeException("Error assigning ADMIN role to first user", e);
        }
    }

    /**
     * Checks if the 'ADMIN' role is needed and assigns it to the first user if it's needed.
     * It first checks if at least one user is registered and if no user has the admin role
     * If the two conditions are met, the method assigns the admin role to the first user
     * @throws RuntimeException If an error occurs during the process.
     */
    public void assignAdminToFirstUserIfNeeded() {
        long userCount = getUserCount();
        boolean hasAdmin = anyUserHasAdminRole();

        if (userCount >= 1 && !hasAdmin) {
            assignAdminToFirstUser();
        }
    }

    /**
     * Returns the list of realm-level role names assigned to the given user (by ID).
     *
     * @param userId The Keycloak ID of the user.
     * @return A List of strings containing the names of the roles.
     */
    public List<String> getRealmLevelRoles(String userId) {
        RealmResource realmResource = keycloak.realm(realm);

        // Récupère l'interface "RoleScopeResource" pour l'utilisateur (realm level)
        RoleScopeResource realmLevelResource =
                realmResource.users().get(userId).roles().realmLevel();

        // Liste des rôles "realm-level" affectés à l'utilisateur
        List<RoleRepresentation> assignedRoles = realmLevelResource.listAll();

        // On renvoie juste le "name" de chaque rôle
        return assignedRoles.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }
}