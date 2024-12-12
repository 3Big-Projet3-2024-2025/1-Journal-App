package be.helha.journalapp.config;

import be.helha.journalapp.security.KeycloakRoleConverter;
import be.helha.journalapp.security.UserSynchronizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserSynchronizationFilter userSynchronizationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/articles/available").permitAll();
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
                    auth.requestMatchers("/newsletters/**").hasAnyRole("ADMIN", "EDITOR");

                    // Articles (autres que /all) : ADMIN, EDITOR, JOURNALIST
                    auth.requestMatchers("/articles/**").hasAnyRole("ADMIN", "EDITOR", "JOURNALIST" ,"READER");

                    // Users : uniquement ADMIN
                    auth.requestMatchers("/users/**").hasRole("ADMIN");

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


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Autoriser toutes les origines
        config.addAllowedHeader("*");        // Autoriser tous les en-têtes
        config.addAllowedMethod("*");        // Autoriser toutes les méthodes HTTP
        config.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
