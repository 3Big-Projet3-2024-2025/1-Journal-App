package be.helha.journalapp.keycloak;

import be.helha.journalapp.service.KeycloakAdminService;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;

/**
 * This class provides a custom event listener for Keycloak to manage user roles upon registration.
 * Specifically, it automatically assigns an "admin" role to the very first user registered in the system.
 * It implements the {@link EventListenerProvider} interface from Keycloak, allowing it to hook into user-related events.
 */
public class AdminRoleEventListenerProvider implements EventListenerProvider {

    private final KeycloakAdminService keycloakAdminService;

    /**
     * Constructor for the AdminRoleEventListenerProvider.
     *
     * @param keycloakAdminService An instance of KeycloakAdminService used to interact with Keycloak for user and role management.
     */
    public AdminRoleEventListenerProvider(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    /**
     * Handles Keycloak events, particularly user registration events.
     * If the event is a user registration and this is the first user in the system,
     * then the "admin" role is assigned to that user.
     *
     * @param event The Keycloak event object containing event details.
     */
    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            String userId = event.getUserId();
            long userCount = keycloakAdminService.getUserCount();
            if (userCount == 1) { // Si c'est le premier utilisateur
                keycloakAdminService.assignAdminToUser(userId);
            }
        }
    }

    /**
     * Handles Keycloak admin events. Currently, this method doesn't implement any action for admin events.
     *
     * @param adminEvent           The Keycloak admin event object containing event details.
     * @param includeRepresentation A boolean indicating whether to include representation details.
     */
    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {

    }

    /**
     * Lifecycle method for the EventListenerProvider, used for any clean-up or resource release.
     * In this case, no cleanup is necessary.
     */
    @Override
    public void close() {

    }
}