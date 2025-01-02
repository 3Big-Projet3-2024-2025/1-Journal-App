package be.helha.journalapp.keycloak;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import be.helha.journalapp.service.KeycloakAdminService;

/**
 * This class is a factory for creating {@link AdminRoleEventListenerProvider} instances.
 * It implements the {@link EventListenerProviderFactory} interface from Keycloak.
 * This factory is responsible for creating and providing an instance of the custom event listener
 * to Keycloak when it's needed. It also provides the logic to obtain the KeycloakAdminService.
 */
public class AdminRoleEventListenerProviderFactory implements EventListenerProviderFactory {

    /**
     * Creates a new instance of {@link AdminRoleEventListenerProvider}.
     * This method is called by Keycloak when an event listener is needed.
     * It obtains an instance of {@link KeycloakAdminService} and uses it to create a new
     * instance of {@link AdminRoleEventListenerProvider}.
     *
     * @param session The Keycloak session object, which may be used to access session related data.
     * @return A new instance of {@link AdminRoleEventListenerProvider}.
     */
    @Override
    public EventListenerProvider create(KeycloakSession session) {
        KeycloakAdminService keycloakAdminService = getKeycloakAdminService();
        return new AdminRoleEventListenerProvider(keycloakAdminService);
    }

    /**
     * Obtains an instance of {@link KeycloakAdminService}.
     * <p>
     * Note: This method is a placeholder and must be implemented to provide a valid instance
     * of {@link KeycloakAdminService}. The implementation should handle obtaining the service instance,
     * which may involve using JNDI, a service locator, dependency injection or other methods.
     *
     * @return An instance of {@link KeycloakAdminService}.
     * @throws UnsupportedOperationException If the method is not implemented.
     */
    private KeycloakAdminService getKeycloakAdminService() {

        throw new UnsupportedOperationException("Implémentation nécessaire pour obtenir KeycloakAdminService");
    }


    /**
     * Initializes the event listener provider factory.
     * This method is called by Keycloak upon initialization.
     * It currently has no implementation.
     *
     * @param config The Keycloak configuration scope.
     */
    @Override
    public void init(org.keycloak.Config.Scope config) {

    }

    /**
     * Performs post-initialization tasks.
     * This method is called by Keycloak after all factories are initialized.
     * It currently has no implementation.
     *
     * @param factory The Keycloak session factory.
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    /**
     * Lifecycle method used for cleaning up resources.
     * It currently has no implementation.
     */
    @Override
    public void close() {
    }


    /**
     * Returns the identifier for this event listener provider factory.
     *
     * @return The identifier string.
     */
    @Override
    public String getId() {
        return "admin-role-event-listener";
    }
}