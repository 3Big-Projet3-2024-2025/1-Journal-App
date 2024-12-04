package be.helha.journalapp.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Récupère les rôles depuis la clé "realm_access" du token JWT
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();  // Retourne une liste vide si aucun rôle n'est trouvé
        }

        // Récupère les rôles de l'objet "realm_access"
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");

        // Mappe les rôles en SimpleGrantedAuthority en ajoutant le préfixe "ROLE_"
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))  // Ajoute "ROLE_" devant le nom du rôle
                .collect(Collectors.toList());
    }
}
