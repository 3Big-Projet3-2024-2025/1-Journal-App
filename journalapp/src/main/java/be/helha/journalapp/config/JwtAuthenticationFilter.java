package be.helha.journalapp.config;

import be.helha.journalapp.util.JwtUtils;
import be.helha.journalapp.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extraire le JWT de la requête
        String jwt = extractJwtFromRequest(request);

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            // Extraire le nom d'utilisateur depuis le JWT
            String username = jwtUtils.getUsernameFromJwt(jwt);

            // Logs pour débogage
            System.out.println("JWT reçu : " + jwt);
            System.out.println("Utilisateur extrait : " + username);

            // Charger les détails de l'utilisateur
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            // Créer l'objet d'authentification
            var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Mettre à jour le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continuer avec le reste de la chaîne de filtres
        filterChain.doFilter(request, response);
    }


    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
