package be.helha.journalapp.config;

import be.helha.journalapp.security.KeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Configuration CORS
                .authorizeHttpRequests(authorizeRequests -> {
                    // Autoriser certaines routes à tout le monde sans authentification
                    authorizeRequests
                            .requestMatchers("/users/**").permitAll();
                    // Permettre l'accès sans authentification à toutes les routes sous /users

                    // Routes avec des rôles spécifiques
                    authorizeRequests
                            .requestMatchers("/newsletters/all").hasRole("ADMIN")
                            .requestMatchers("/images/all").hasRole("ADMIN")
                            .requestMatchers("/comments/all").hasRole("ADMIN")
                            .requestMatchers("/articles").hasRole("ADMIN")
                            .requestMatchers("/images").hasRole("ADMIN");

                    // Routes Swagger accessibles sans authentification
                    authorizeRequests
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs", "/articles/all", "/newsletters/all").permitAll();

                    // Toutes les autres routes nécessitent une authentification
                    authorizeRequests.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
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
        config.addAllowedOriginPattern("*");  // Autoriser toutes les origines
        config.addAllowedHeader("*");        // Autoriser tous les en-têtes
        config.addAllowedMethod("*");        // Autoriser toutes les méthodes HTTP
        config.addExposedHeader("Authorization"); // Permettre l'exposition de l'en-tête Authorization
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
