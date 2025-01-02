package be.helha.journalapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * This service provides methods for managing users, including creating users
 * and assigning the admin role to the first user in the system.
 */
@Service
public class UserService {

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    /**
     * Creates a new user and assigns the 'ADMIN' role if the user is the first to be created.
     *
     * This method first adds the user to Keycloak using the {@link KeycloakAdminService#addUserToKeycloak(Map)}
     * method, then it retrieves the amount of users in keycloak, if the count is 1 it assigns the admin role to the user
     * and returns the user ID of the created user.
     *
     * @param userDetails A map containing the details of the user to be created.
     * @return The ID of the newly created user in Keycloak.
     */
    public String createUser(Map<String, Object> userDetails) {
        String userId = keycloakAdminService.addUserToKeycloak(userDetails);
        long userCount = keycloakAdminService.getUserCount();
        if (userCount == 1) { // Si c'est le premier utilisateur
            keycloakAdminService.assignAdminToUser(userId);
        }
        return userId;
    }
}