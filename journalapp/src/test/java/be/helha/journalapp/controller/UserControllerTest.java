package be.helha.journalapp.controller;

import be.helha.journalapp.model.Role;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.RoleRepository;
import be.helha.journalapp.repositories.UserRepository;
import be.helha.journalapp.service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires pour {@link UserController}.
 * Utilise Mockito pour simuler les dépendances et JUnit 5 pour les tests.
 */
class UserControllerTest {

    /**
     * Mocked EmailService pour simuler l'envoi d'emails.
     */
    @Mock
    private EmailService emailService;

    /**
     * Mocked UserRepository pour simuler les opérations sur les utilisateurs.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked RoleRepository pour simuler les opérations sur les rôles.
     */
    @Mock
    private RoleRepository roleRepository;

    /**
     * Instance du UserController à tester avec les mocks injectés.
     */
    @InjectMocks
    private UserController userController;

    /**
     * Objet de test pour User.
     */
    private User testUser;

    /**
     * Objet de test pour Role.
     */
    private Role testRole;

    /**
     * Objet de test pour Authentication.
     */
    @Mock
    private Authentication authentication;

    /**
     * Objet de test pour SecurityContext.
     */
    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        // Initialisation des mocks
        MockitoAnnotations.openMocks(this);

        // Initialisation des données de test
        testRole = new Role();
        testRole.setRoleId(1L);
        testRole.setRoleName("ADMIN");

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setAuthorized(true);
        testUser.setRoleChange(false);
        testUser.setKeycloakId("john-keycloak-id");
        testUser.setRole(testRole);
        testUser.setArticles(new ArrayList<>());
        testUser.setNewsletters(new ArrayList<>());
        testUser.setArticleReads(new ArrayList<>());

        // Configuration du SecurityContextHolder pour les tests impliquant la sécurité
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // -------------------- Tests pour getAllUsers --------------------
    /**
     * Test de la récupération réussie de tous les utilisateurs.
     */
    @Test
    void getAllUsers_Success() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
        verify(userRepository).findAll();
    }

    // -------------------- Tests pour getUserById --------------------
    /**
     * Test de la récupération réussie d'un utilisateur par ID.
     */
    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<User> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        verify(userRepository).findById(1L);
    }

    /**
     * Test de la récupération d'un utilisateur par ID non existant.
     */
    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getUserById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository).findById(999L);
    }

    // -------------------- Tests pour deleteUser --------------------
    /**
     * Test de la suppression réussie d'un utilisateur existant.
     */
    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        ResponseEntity<String> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userRepository).deleteById(1L);
    }

    /**
     * Test de la suppression d'un utilisateur non existant.
     */
    @Test
    void deleteUser_NotFound() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act
        ResponseEntity<String> response = userController.deleteUser(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userRepository, never()).deleteById(anyLong());
    }

    // -------------------- Tests pour forgotPassword --------------------
    /**
     * Test de l'envoi réussi d'un email de réinitialisation de mot de passe.
     */
    @Test
    void forgotPassword_Success() throws MessagingException {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = userController.forgotPassword("john.doe@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password reset email sent", response.getBody());
        verify(emailService).sendEmail(eq("john.doe@example.com"), anyString(), anyString());
    }

    /**
     * Test de l'envoi d'un email de réinitialisation de mot de passe à un utilisateur non existant.
     */
    @Test
    void forgotPassword_UserNotFound() throws MessagingException {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = userController.forgotPassword("unknown@example.com");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User with the specified email not found", response.getBody());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    /**
     * Test de l'échec de l'envoi d'un email de réinitialisation de mot de passe.
     */
    @Test
    void forgotPassword_EmailSendingFailed() throws MessagingException {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        doThrow(new MessagingException("Failed to send email")).when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = userController.forgotPassword("john.doe@example.com");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to send password reset email", response.getBody());
        verify(emailService).sendEmail(eq("john.doe@example.com"), anyString(), anyString());
    }

    // -------------------- Tests pour changeUserRole --------------------
    /**
     * Test de la mise à jour réussie du rôle d'un utilisateur.
     */
    @Test
    void changeUserRole_Success() {
        // Arrange
        Role newRole = new Role();
        newRole.setRoleId(2L);
        newRole.setRoleName("USER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(newRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<User> response = userController.changeUserRole(1L, "USER");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER", response.getBody().getRole().getRoleName());
        assertTrue(response.getBody().isRoleChange());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test de la mise à jour du rôle d'un utilisateur non existant.
     */
    @Test
    void changeUserRole_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.changeUserRole(999L, "USER");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test de la mise à jour du rôle d'un utilisateur avec un rôle non existant.
     */
    @Test
    void changeUserRole_RoleNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.changeUserRole(1L, "NON_EXISTENT_ROLE");
        });

        assertEquals("Role not found: NON_EXISTENT_ROLE", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // -------------------- Tests pour authorizeUser --------------------
    /**
     * Test de l'autorisation réussie d'un utilisateur.
     */
    @Test
    void authorizeUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<User> response = userController.authorizeUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isAuthorized());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test de l'autorisation d'un utilisateur non existant.
     */
    @Test
    void authorizeUser_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.authorizeUser(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }

    // -------------------- Tests pour getCurrentUser(Authentication) --------------------
    /**
     * Test de la récupération réussie du principal de l'utilisateur authentifié.
     */
    @Test
    void getCurrentUser_Success() {
        // Arrange
        User principalUser = testUser;
        when(authentication.getPrincipal()).thenReturn(principalUser);

        // Act
        ResponseEntity<?> response = userController.getCurrentUser(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(principalUser, response.getBody());
    }

    // -------------------- Tests pour getUserByKeycloakId --------------------
    /**
     * Test de la récupération réussie d'un utilisateur par son Keycloak ID.
     */
    @Test
    void getUserByKeycloakId_Success() {
        // Arrange
        when(userRepository.findByKeycloakId("john-keycloak-id")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<User> response = userController.getUserByKeycloakId("john-keycloak-id");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        verify(userRepository).findByKeycloakId("john-keycloak-id");
    }

    /**
     * Test de la récupération d'un utilisateur par un Keycloak ID non existant.
     */
    @Test
    void getUserByKeycloakId_NotFound() {
        // Arrange
        when(userRepository.findByKeycloakId("unknown-keycloak-id")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getUserByKeycloakId("unknown-keycloak-id");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository).findByKeycloakId("unknown-keycloak-id");
    }

    // -------------------- Tests pour getCurrentUser() --------------------
    /**
     * Test de la récupération réussie de l'utilisateur actuel depuis le SecurityContext.
     */
    @Test
    void getCurrentUser_FromSecurityContext_Success() {
        // Arrange
        when(authentication.getName()).thenReturn("john-keycloak-id");
        when(userRepository.findByKeycloakId("john-keycloak-id")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<User> response = userController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        verify(userRepository).findByKeycloakId("john-keycloak-id");
    }

    /**
     * Test de la récupération de l'utilisateur actuel depuis le SecurityContext avec un Keycloak ID non existant.
     */
    @Test
    void getCurrentUser_FromSecurityContext_NotFound() {
        // Arrange
        when(authentication.getName()).thenReturn("unknown-keycloak-id");
        when(userRepository.findByKeycloakId("unknown-keycloak-id")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository).findByKeycloakId("unknown-keycloak-id");
    }
}
