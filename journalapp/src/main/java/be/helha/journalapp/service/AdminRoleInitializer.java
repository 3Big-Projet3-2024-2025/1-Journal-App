package be.helha.journalapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * This component is responsible for initializing and ensuring that the admin role is assigned
 * to the first user in the system. It does this by periodically checking and assigning
 * the admin role if it hasn't been assigned yet.
 * <p>
 * It uses Spring's scheduling to perform this check on a regular basis.
 */
@Component
public class AdminRoleInitializer {

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    /**
     * Periodically checks and assigns the admin role to the first user if needed.
     * This method is scheduled to run every 30 seconds. It attempts to assign
     * the admin role using the {@link KeycloakAdminService#assignAdminToFirstUserIfNeeded()} method
     * and logs the outcome. It also handles and logs any exceptions that might occur during the process.
     */
    @Scheduled(fixedDelay = 30000) // Toutes les 30 secondes
    public void initializeAdminRole() {
        try {
            keycloakAdminService.assignAdminToFirstUserIfNeeded();
            System.out.println("Vérification de l'assignation du rôle ADMIN effectuée.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'assignation du rôle ADMIN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}