package be.helha.journalapp.config;

import be.helha.journalapp.security.KeycloakRoleConverter;
import be.helha.journalapp.security.UserSynchronizationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for Spring Security settings.
 * Enables web security and configures authentication and authorization rules for the application.
 * This configuration is enabled if the property 'security.enabled' is set to 'true' or is missing.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(
        name = "security.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * This method sets up CORS, authorization rules, OAuth2 resource server configuration,
     * and adds the user synchronization filter.
     *
     * @param http                      The HttpSecurity object to configure.
     * @param userSynchronizationFilter The custom filter for synchronizing users.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserSynchronizationFilter userSynchronizationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/articles/available").permitAll();
                    auth.requestMatchers("/articles/unavailable").permitAll();
                    auth.requestMatchers("/articles/all").permitAll();
                    auth.requestMatchers("/newsletters/all").permitAll();
                    auth.requestMatchers("/articles/search").permitAll();
                    auth.requestMatchers("/images/article/").permitAll();



                    // Lecture des commentaires accessible à tous
                    auth.requestMatchers(HttpMethod.GET, "/comments/**").permitAll();

                    // Ajout de commentaires réservé aux utilisateurs authentifiés
                    auth.requestMatchers(HttpMethod.POST, "/comments").authenticated();

                    // Pour la mise à jour ou la suppression des commentaires (si nécessaire), on peut restreindre à ADMIN
                    auth.requestMatchers(HttpMethod.PUT, "/comments/**").hasAnyRole("ADMIN", "EDITOR");
                    auth.requestMatchers(HttpMethod.DELETE, "/comments/**").hasAnyRole("ADMIN", "EDITOR");

                    // Contrôles d'accès par rôle pour les autres endpoints :
                    // Comments: déjà traités ci-dessus
                    // Newsletters (autres que /all) : ADMIN, EDITOR
                    auth.requestMatchers("/newsletters/**").hasAnyRole("ADMIN", "EDITOR", "JOURNALIST" ,"READER");

                    // Articles (autres que /all) : ADMIN, EDITOR, JOURNALIST
                    auth.requestMatchers("/articles/**").hasAnyRole("ADMIN", "EDITOR", "JOURNALIST" ,"READER");

                    // Users : uniquement ADMIN
                    auth.requestMatchers("/users/**").hasAnyRole("ADMIN", "EDITOR");

                    // Roles : uniquement ADMIN
                    auth.requestMatchers("/roles/**").hasRole("ADMIN");

                    // Images : ADMIN, EDITOR, JOURNALIST
                    auth.requestMatchers("/images/**").hasAnyRole("ADMIN", "EDITOR", "JOURNALIST");
                    auth.requestMatchers("/swagger-ui/**", "/v3/api-docs").permitAll();


                    // Toute autre requête nécessite une authentification
                    auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .addFilterAfter(userSynchronizationFilter, org.springframework.security.web.access.intercept.AuthorizationFilter.class)
                .build();
    }


    /**
     * Configures the JWT authentication converter to use the Keycloak role converter.
     * This converter is responsible for mapping roles from Keycloak's JWT to Spring Security authorities.
     *
     * @return The configured JwtAuthenticationConverter.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    /**
     * Configures the CORS (Cross-Origin Resource Sharing) settings for the application.
     * This method allows all origins, headers, and methods and exposes the 'Authorization' header.
     *
     * @return The configured UrlBasedCorsConfigurationSource.
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // All origins
        config.addAllowedHeader("*");        // All headers
        config.addAllowedMethod("*");        // All HTTP method
        config.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
