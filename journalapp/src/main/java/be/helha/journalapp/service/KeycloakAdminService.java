package be.helha.journalapp.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Service
public class KeycloakAdminService {

    private final RestTemplate restTemplate;
    private final String keycloakUrl = "http://localhost:8082/admin/realms/journalapp/users";

    public KeycloakAdminService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", "admin-cli");
        params.add("username", "admin");
        params.add("password", "admin");
        params.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "http://localhost:8082/realms/master/protocol/openid-connect/token",
                request,
                Map.class
        );

        return response.getBody().get("access_token").toString();
    }

    public String addUserToKeycloak(Map<String, Object> userDetails) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userDetails, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakUrl,
                request,
                Void.class
        );

        // Récupérer l'ID utilisateur dans Keycloak à partir de l'en-tête "Location"
        String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
        if (location != null) {
            return location.substring(location.lastIndexOf("/") + 1);
        }
        throw new RuntimeException("Failed to create user in Keycloak");
    }


    public void updateUserInKeycloak(String keycloakId, Map<String, Object> userUpdates) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userUpdates, headers);
        restTemplate.exchange(
                keycloakUrl + "/" + keycloakId,
                HttpMethod.PUT,
                request,
                Void.class
        );
    }

    public void deleteUserInKeycloak(String keycloakId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        restTemplate.exchange(
                keycloakUrl + "/" + keycloakId,
                HttpMethod.DELETE,
                request,
                Void.class
        );
    }



}
