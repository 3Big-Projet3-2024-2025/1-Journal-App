package be.helha.journalapp.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * This class converts Keycloak roles from a JWT (JSON Web Token) into a collection of Spring Security GrantedAuthority objects.
 * It is used to map Keycloak roles to Spring Security roles.
 */
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {


    /**
     * Converts the Keycloak roles within a JWT to a Collection of GrantedAuthority objects.
     * Extracts roles from the "realm_access" claim and converts them to Spring Security role format ("ROLE_ROLENAME").
     *
     * @param jwt The JSON Web Token containing the roles.
     * @return A Collection of GrantedAuthority representing the user's roles.
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}
