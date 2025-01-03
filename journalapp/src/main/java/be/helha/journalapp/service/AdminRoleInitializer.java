package be.helha.journalapp.service;

import be.helha.journalapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for initializing and ensuring that the admin role is assigned
 * to the first user in the system.
 * It uses Spring's scheduling to perform this check on a regular basis.
 */
@Component
public class AdminRoleInitializer {

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @Autowired
    private KeycloakSynchronizationService keycloakSynchronizationService;

    @Autowired
    private UserRepository userRepository; // ← Pour accéder à la DB

    @Scheduled(fixedDelay = 30000)
    public void initializeAdminRole() {
        try {
            // (Optionnel) Si tu veux toujours exécuter cette méthode :
            keycloakAdminService.assignAdminToFirstUserIfNeeded();

            // -- 1) On récupère l'utilisateur qui a l'id=1 dans la DB locale
            Long localUserId = 1L;
            var userOptional = userRepository.findById(localUserId);

            if (userOptional.isPresent()) {
                var user = userOptional.get();
                String keycloakId = user.getKeycloakId();

                if (keycloakId != null && !keycloakId.isEmpty()) {
                    // -- 2) On synchronise les rôles Keycloak vers la DB
                    keycloakSynchronizationService.syncUserRolesFromKeycloak(keycloakId);
                } else {
                    System.out.println("L'utilisateur local ID=1 n'a pas de keycloakId enregistré.");
                }
            } else {
                System.out.println("Aucun utilisateur local trouvé avec l'id=1.");
            }

            System.out.println("Vérification de l'assignation du rôle ADMIN effectuée.");

        } catch (Exception e) {
            System.err.println("Erreur lors de l'assignation du rôle ADMIN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
