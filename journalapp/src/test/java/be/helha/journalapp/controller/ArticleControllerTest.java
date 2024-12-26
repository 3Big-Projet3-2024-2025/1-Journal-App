package be.helha.journalapp.controller;

import be.helha.journalapp.model.*;
import be.helha.journalapp.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * This class contains unit tests for the {@link ArticleController}.
 * It uses Mockito for mocking dependencies and JUnit 5 for testing.
 */
@SpringBootTest
class ArticleControllerTest {

    /**
     * Mocked repository for articles.
     */
    @Mock
    private ArticleRepository articleRepository;

    /**
     * Mocked repository for users.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked repository for newsletters.
     */
    @Mock
    private NewsletterRepository newsletterRepository;

    /**
     * Mocked repository for user article read status.
     */
    @Mock
    private UserArticleReadRepository userArticleReadRepository;


    /**
     * Mocked authentication object.
     */
    @Mock
    private Authentication authentication;

    /**
     * The ArticleController instance to be tested.
     */
    @InjectMocks
    private ArticleController articleController;

    /**
     * MockMvc for simulating HTTP requests.
     */
    private MockMvc mockMvc;

    /**
     * Test article object.
     */
    private Article testArticle;

    /**
     * Test user object.
     */
    private User testUser;

    /**
     * Test newsletter object.
     */
    private Newsletter testNewsletter;

    /**
     * Test user article read object.
     */
    private UserArticleRead testUserArticleRead;


    /**
     * Sets up the test environment before each test method.
     * It initializes the MockMvc, test data for articles, users, and newsletters.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();

        // Initialisation des données de test
        testNewsletter = new Newsletter();
        testNewsletter.setNewsletterId(1L);
        testNewsletter.setTitle("Test Newsletter");
        testNewsletter.setBackgroundColor("#FFFFFF");

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setKeycloakId("test-keycloak-id");

        testArticle = new Article();
        testArticle.setArticleId(1L);
        testArticle.setTitle("Test Article");
        testArticle.setContent("Test Content");
        testArticle.setPublicationDate("2024-01-01");
        testArticle.setLatitude(50.0);
        testArticle.setLongitude(4.0);
        testArticle.setValid(true);
        testArticle.setNewsletter(testNewsletter);
        testArticle.setAuthor(testUser);

        testUserArticleRead = new UserArticleRead();
        testUserArticleRead.setUser(testUser);
        testUserArticleRead.setArticle(testArticle);
        testUserArticleRead.setRead(true);
    }

    /**
     * Tests the successful addition of an article.
     */
    @Test
    void addArticle_Success() {
        // Arrange
        Map<String, Object> articleData = new HashMap<>();
        articleData.put("title", "Test Article");
        articleData.put("content", "Test Content");
        articleData.put("publicationDate", "2024-01-01");
        articleData.put("longitude", 4.0);
        articleData.put("latitude", 50.0);
        articleData.put("valid", true);
        articleData.put("newsletter_id", 1L);
        articleData.put("user_id", 1L);

        when(newsletterRepository.findById(1L)).thenReturn(Optional.of(testNewsletter));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        // Act
        ResponseEntity<Article> response = articleController.addArticle(articleData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(articleRepository).save(any(Article.class));
    }

    /**
     * Tests the successful retrieval of all articles.
     */
    @Test
    void getAllArticles_Success() {
        // Arrange
        when(articleRepository.findAll()).thenReturn(List.of(testArticle));

        // Act
        ResponseEntity<List<Article>> response = articleController.getAllArticles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(articleRepository).findAll();
    }

    /**
     * Tests the successful retrieval of an article by its ID.
     */
    @Test
    void getArticleById_Success() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));

        // Act
        ResponseEntity<Article> response = articleController.getArticleById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(articleRepository).findById(1L);
    }

    /**
     * Tests the scenario where an article is not found by its ID.
     */
    @Test
    void getArticleById_NotFound() {
        // Arrange
        when(articleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Article> response = articleController.getArticleById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(articleRepository).findById(999L);
    }

    /**
     * Tests the successful update of an existing article.
     */
    @Test
    void updateArticle_Success() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        // Act
        ResponseEntity<Article> response = articleController.updateArticle(1L, testArticle);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(articleRepository).save(any(Article.class));
    }

    /**
     * Tests the successful deletion of an article.
     */
    @Test
    void deleteArticle_Success() {
        // Arrange
        when(articleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(articleRepository).deleteById(1L);

        // Act
        ResponseEntity<String> response = articleController.deleteArticle(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Article deleted successfully.", response.getBody());
        verify(articleRepository).deleteById(1L);
    }

    /**
     * Tests the successful validation of an article.
     */
    @Test
    void validateArticle_Success() {
        // Arrange
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        // Act
        ResponseEntity<Article> response = articleController.validateArticle(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isValid());
        verify(articleRepository).save(any(Article.class));
    }

    /**
     * Tests the successful marking of an article as read by a user.
     */
    @Test
    void markAsRead_Success() {
        // Arrange
        when(authentication.getName()).thenReturn("test-keycloak-id");
        when(userRepository.findByKeycloakId("test-keycloak-id")).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(userArticleReadRepository.findByUserUserIdAndArticleArticleId(1L, 1L))
                .thenReturn(Optional.of(testUserArticleRead));
        when(userArticleReadRepository.save(any(UserArticleRead.class))).thenReturn(testUserArticleRead);

        // Act
        ResponseEntity<Map<String, String>> response = articleController.markAsRead(1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Article marked as read.", response.getBody().get("message"));
        verify(userArticleReadRepository).save(any(UserArticleRead.class));
    }

    /**
     * Tests the successful retrieval of an article read status for a user.
     */
    @Test
    void getArticleReadStatus_Success() {
        // Arrange
        when(authentication.getName()).thenReturn("test-keycloak-id");
        when(userRepository.findByKeycloakId("test-keycloak-id")).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(userArticleReadRepository.findByUserUserIdAndArticleArticleId(1L, 1L))
                .thenReturn(Optional.of(testUserArticleRead));

        // Act
        ResponseEntity<Map<String, Boolean>> response = articleController.getArticleReadStatus(1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("isRead"));
        verify(userArticleReadRepository).findByUserUserIdAndArticleArticleId(1L, 1L);
    }

    /**
     * Tests the successful retrieval of all articles marked as read by a user.
     */
    @Test
    void getReadArticles_Success() {
        // Arrange
        when(authentication.getName()).thenReturn("test-keycloak-id");
        when(userRepository.findByKeycloakId("test-keycloak-id")).thenReturn(Optional.of(testUser));
        when(userArticleReadRepository.findByUserUserIdAndIsReadTrue(1L))
                .thenReturn(List.of(testUserArticleRead));

        // Act
        ResponseEntity<List<Article>> response = articleController.getReadArticles(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userArticleReadRepository).findByUserUserIdAndIsReadTrue(1L);
    }

    /**
     * Tests the successful retrieval of articles by author's email.
     */
    @Test
    void getArticlesByAuthorEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(articleRepository.findByAuthorId(1L)).thenReturn(List.of(testArticle));

        // Act
        ResponseEntity<List<Article>> response = articleController.getArticlesByAuthorEmail("john@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(articleRepository).findByAuthorId(1L);
    }

    /**
     * Tests the successful retrieval of newsletter background color.
     */
    @Test
    void getNewsletterBackgroundColor_Success() {
        // Arrange
        when(newsletterRepository.findById(1L)).thenReturn(Optional.of(testNewsletter));

        // Act
        ResponseEntity<Map<String, String>> response = articleController.getNewsletterBackgroundColor(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("#FFFFFF", response.getBody().get("backgroundColor"));
        verify(newsletterRepository).findById(1L);
    }
}