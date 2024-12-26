package be.helha.journalapp.security;

import be.helha.journalapp.model.Role;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.RoleRepository;
import be.helha.journalapp.repositories.UserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * This filter synchronizes user data from Keycloak with the local database.
 * It is enabled only if the 'security.enabled' property is set to 'true' (or is missing).
 */
@ConditionalOnProperty(
        value = "security.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Component
public class UserSynchronizationFilter implements Filter {

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Repository for accessing role data.
     */
    private final RoleRepository roleRepository;


    /**
     * Constructor for the UserSynchronizationFilter.
     * @param userRepository The repository for user data.
     * @param roleRepository The repository for role data.
     */
    public UserSynchronizationFilter(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    /**
     * This method intercepts incoming requests to synchronize user data.
     * It checks for a valid JWT token in the authentication, extracts user information,
     * and creates a new user or updates an existing one if necessary.
     * @param request The incoming servlet request.
     * @param response The outgoing servlet response.
     * @param chain The filter chain for processing.
     * @throws IOException If an I/O error occurs.
     * @throws ServletException If a servlet error occurs.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String sub = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            String givenName = jwt.getClaimAsString("given_name");
            String familyName = jwt.getClaimAsString("family_name");

            if (sub != null) {
                Optional<User> existingUser = userRepository.findByKeycloakId(sub);
                if (existingUser.isEmpty()) {
                    User newUser = new User();
                    newUser.setKeycloakId(sub);
                    newUser.setEmail(email);
                    newUser.setFirstName(givenName);
                    newUser.setLastName(familyName);

                    // Récupération des rôles Keycloak
                    Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
                    if (realmAccess != null && realmAccess.get("roles") instanceof List<?> rolesList) {
                        @SuppressWarnings("unchecked")
                        List<String> roles = (List<String>) rolesList;

                        String mainRole = determineMainRole(roles);
                        Role dbRole = roleRepository.findByRoleName(mainRole)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + mainRole));
                        newUser.setRole(dbRole);
                    } else {
                        // Pas de rôles fournis, rôle par défaut
                        Role defaultRole = roleRepository.findByRoleName("READER")
                                .orElseThrow(() -> new RuntimeException("Default role not found."));
                        newUser.setRole(defaultRole);
                    }

                    userRepository.save(newUser);
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Determines the main user role based on a hierarchy: ADMIN > EDITOR > JOURNALIST > READER.
     * If no matching role is found, it defaults to "READER".
     * @param roles A list of role names.
     * @return The main role based on the hierarchy, or "READER" if no match is found.
     */
    private String determineMainRole(List<String> roles) {
        if (roles.contains("ADMIN")) {
            return "ADMIN";
        } else if (roles.contains("EDITOR")) {
            return "EDITOR";
        } else if (roles.contains("JOURNALIST")) {
            return "JOURNALIST";
        } else if (roles.contains("READER")) {
            return "READER";
        } else {
            // Si aucun rôle correspondant, on peut retourner READER par défaut
            return "READER";
        }
    }
}
