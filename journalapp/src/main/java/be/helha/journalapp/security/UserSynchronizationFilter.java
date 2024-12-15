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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UserSynchronizationFilter implements Filter {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserSynchronizationFilter(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

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
     * Détermine le rôle principal de l'utilisateur en fonction de la hiérarchie :
     * ADMIN > EDITOR > JOURNALIST > READER
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
