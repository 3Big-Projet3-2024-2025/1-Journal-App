package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.NewsletterRepository;
import be.helha.journalapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the {@link NewsletterController}.
 * It uses Mockito for mocking dependencies and JUnit 5 for testing.
 */
@SpringBootTest
class NewsletterControllerTest {

    /**
     * Mocked repository for newsletters.
     */
    @Mock
    private NewsletterRepository newsletterRepository;

    /**
     * Mocked repository for users.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked repository for articles.
     */
    @Mock
    private ArticleRepository articleRepository;

    /**
     * The NewsletterController instance to be tested.
     */
    @InjectMocks
    private NewsletterController newsletterController;

    /**
     * Test newsletter object.
     */
    private Newsletter testNewsletter;

    /**
     * Test user object.
     */
    private User testUser;

    /**
     * Test article object.
     */
    private Article testArticle;

    /**
     * Sets up the test environment before each test method.
     * It initializes test data for newsletters, users, and articles.
     */
    @BeforeEach
    void setUp() {
        // Initialize test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setEmail("jane.doe@example.com");
        testUser.setKeycloakId("jane-keycloak-id");

        // Initialize test newsletter
        testNewsletter = new Newsletter();
        testNewsletter.setNewsletterId(1L);
        testNewsletter.setTitle("Tech Weekly");
        testNewsletter.setSubtitle("Latest Tech News");
        testNewsletter.setPublicationDate("2024-05-01");
        testNewsletter.setBackgroundColor("#000000");
        testNewsletter.setTitleFont("Arial");
        testNewsletter.setTitleFontSize(24);
        testNewsletter.setTitleColor("#FFFFFF");
        testNewsletter.setTitleBold(true);
        testNewsletter.setTitleUnderline(false);
        testNewsletter.setSubtitleFont("Calibri");
        testNewsletter.setSubtitleFontSize(18);
        testNewsletter.setSubtitleColor("#CCCCCC");
        testNewsletter.setSubtitleBold(false);
        testNewsletter.setSubtitleItalic(true);
        testNewsletter.setTextAlign("left");
        testNewsletter.setCreator(testUser);
        testNewsletter.setJournalists(new ArrayList<>());
        testNewsletter.setArticles(new ArrayList<>());

        // Initialize test article
        testArticle = new Article();
        testArticle.setArticleId(1L);
        testArticle.setTitle("New Java Features");
        testArticle.setContent("Details about new features in Java.");
        testArticle.setPublicationDate("2024-04-25");
        testArticle.setLatitude(40.7128);
        testArticle.setLongitude(-74.0060);
        testArticle.setValid(true);
        testArticle.setNewsletter(testNewsletter);
        testArticle.setAuthor(testUser);
    }

    /**
     * Tests the successful addition of a newsletter.
     */
    @Test
    void addNewsletter_Success() {
        // Arrange
        Map<String, Object> newsletterData = new HashMap<>();
        newsletterData.put("title", "Tech Weekly");
        newsletterData.put("subtitle", "Latest Tech News");
        newsletterData.put("publicationDate", "2024-05-01");
        newsletterData.put("backgroundColor", "#000000");
        newsletterData.put("titleFont", "Arial");
        newsletterData.put("titleFontSize", 24);
        newsletterData.put("titleColor", "#FFFFFF");
        newsletterData.put("titleBold", true);
        newsletterData.put("titleUnderline", false);
        newsletterData.put("subtitleFont", "Calibri");
        newsletterData.put("subtitleFontSize", 18);
        newsletterData.put("subtitleColor", "#CCCCCC");
        newsletterData.put("subtitleBold", false);
        newsletterData.put("subtitleItalic", true);
        newsletterData.put("textAlign", "left");
        newsletterData.put("creator", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(newsletterRepository.save(any(Newsletter.class))).thenReturn(testNewsletter);

        // Act
        ResponseEntity<?> response = newsletterController.addNewsletter(newsletterData);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        Newsletter savedNewsletter = (Newsletter) response.getBody();
        assertEquals("Tech Weekly", savedNewsletter.getTitle());
        verify(newsletterRepository).save(any(Newsletter.class));
    }

    /**
     * Tests adding a newsletter with missing creator ID.
     */
    @Test
    void addNewsletter_MissingCreator() {
        // Arrange
        Map<String, Object> newsletterData = new HashMap<>();
        newsletterData.put("title", "Tech Weekly");

        // Act
        ResponseEntity<?> response = newsletterController.addNewsletter(newsletterData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Creator ID is missing from request.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests adding a newsletter with invalid creator ID format.
     */
    @Test
    void addNewsletter_InvalidCreatorIdFormat() {
        // Arrange
        Map<String, Object> newsletterData = new HashMap<>();
        newsletterData.put("title", "Tech Weekly");
        newsletterData.put("creator", "invalid-id");

        // Act
        ResponseEntity<?> response = newsletterController.addNewsletter(newsletterData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Invalid creator ID format.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests adding a newsletter with a non-existent creator.
     */
    @Test
    void addNewsletter_CreatorNotFound() {
        // Arrange
        Map<String, Object> newsletterData = new HashMap<>();
        newsletterData.put("title", "Tech Weekly");
        newsletterData.put("creator", 999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.addNewsletter(newsletterData);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("User with ID 999 not found.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests the successful retrieval of all newsletters.
     */
    @Test
    void getAllNewsletters_Success() {
        // Arrange
        when(newsletterRepository.findAll()).thenReturn(List.of(testNewsletter));

        // Act
        ResponseEntity<List<Newsletter>> response = newsletterController.getAllNewsletters();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(newsletterRepository).findAll();
    }

    /**
     * Tests the successful retrieval of a newsletter by its ID.
     */
    @Test
    void getNewsletterById_Success() {
        // Arrange
        when(newsletterRepository.findById(1L)).thenReturn(Optional.of(testNewsletter));

        // Act
        ResponseEntity<?> response = newsletterController.getNewsletterById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Newsletter newsletter = (Newsletter) response.getBody();
        assertNotNull(newsletter);
        assertEquals("Tech Weekly", newsletter.getTitle());
        verify(newsletterRepository).findById(1L);
    }

    /**
     * Tests the scenario where a newsletter is not found by its ID.
     */
    @Test
    void getNewsletterById_NotFound() {
        // Arrange
        when(newsletterRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.getNewsletterById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(newsletterRepository).findById(999L);
    }

    /**
     * Tests the successful update of an existing newsletter.
     */
    @Test
    void updateNewsletter_Success() {
        // Arrange
        Newsletter updatedNewsletter = new Newsletter();
        updatedNewsletter.setTitle("Tech Monthly");
        updatedNewsletter.setSubtitle("Monthly Tech Insights");
        updatedNewsletter.setPublicationDate("2024-06-01");
        updatedNewsletter.setBackgroundColor("#111111");
        updatedNewsletter.setTitleFont("Verdana");
        updatedNewsletter.setTitleFontSize(26);
        updatedNewsletter.setTitleColor("#EEEEEE");
        updatedNewsletter.setTitleBold(false);
        updatedNewsletter.setTitleUnderline(true);
        updatedNewsletter.setSubtitleFont("Tahoma");
        updatedNewsletter.setSubtitleFontSize(20);
        updatedNewsletter.setSubtitleColor("#DDDDDD");
        updatedNewsletter.setSubtitleBold(true);
        updatedNewsletter.setSubtitleItalic(false);
        updatedNewsletter.setTextAlign("center");

        when(newsletterRepository.findById(1L)).thenReturn(Optional.of(testNewsletter));
        when(newsletterRepository.save(any(Newsletter.class))).thenReturn(updatedNewsletter);
        when(articleRepository.findByNewsletterNewsletterId(1L)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<?> response = newsletterController.updateNewsletter(1L, updatedNewsletter);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Newsletter savedNewsletter = (Newsletter) response.getBody();
        assertNotNull(savedNewsletter);
        assertEquals("Tech Monthly", savedNewsletter.getTitle());
        verify(newsletterRepository).save(any(Newsletter.class));
    }

    /**
     * Tests updating a non-existent newsletter.
     */
    @Test
    void updateNewsletter_NotFound() {
        // Arrange
        Newsletter updatedNewsletter = new Newsletter();
        updatedNewsletter.setTitle("Tech Monthly");

        when(newsletterRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.updateNewsletter(999L, updatedNewsletter);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests the successful deletion of a newsletter.
     */
    @Test
    void deleteNewsletter_Success() {
        // Arrange
        when(newsletterRepository.existsById(1L)).thenReturn(true);
        doNothing().when(newsletterRepository).deleteById(1L);

        // Act
        ResponseEntity<?> response = newsletterController.deleteNewsletter(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter deleted successfully", body.get("message"));
        verify(newsletterRepository).deleteById(1L);
    }

    /**
     * Tests deleting a non-existent newsletter.
     */
    @Test
    void deleteNewsletter_NotFound() {
        // Arrange
        when(newsletterRepository.existsById(999L)).thenReturn(false);

        // Act
        ResponseEntity<?> response = newsletterController.deleteNewsletter(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(newsletterRepository, never()).deleteById(anyLong());
    }

    /**
     * Tests checking if a user is a journalist in a newsletter (positive case).
     */
    @Test
    void isJournalistInNewsletter_True() {
        // Arrange
        Long newsletterId = 1L;
        Long userId = 1L;
        testNewsletter.getJournalists().add(testUser);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));

        // Act
        ResponseEntity<?> response = newsletterController.isJournalistInNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Boolean> body = (Map<String, Boolean>) response.getBody();
        assertTrue(body.get("isJournalist"));
        verify(newsletterRepository).findById(newsletterId);
    }

    /**
     * Tests checking if a user is a journalist in a newsletter (negative case).
     */
    @Test
    void isJournalistInNewsletter_False() {
        // Arrange
        Long newsletterId = 1L;
        Long userId = 2L; // Different user

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));

        // Act
        ResponseEntity<?> response = newsletterController.isJournalistInNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Boolean> body = (Map<String, Boolean>) response.getBody();
        assertFalse(body.get("isJournalist"));
        verify(newsletterRepository).findById(newsletterId);
    }

    /**
     * Tests checking if a user is a journalist in a non-existent newsletter.
     */
    @Test
    void isJournalistInNewsletter_NewsletterNotFound() {
        // Arrange
        Long newsletterId = 999L;
        Long userId = 1L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.isJournalistInNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(newsletterRepository).findById(newsletterId);
    }

    /**
     * Tests retrieving newsletters for a specific journalist.
     */
    @Test
    void getNewslettersForJournalist_Success() {
        // Arrange
        Long userId = 1L;
        when(newsletterRepository.findByJournalistUserId(userId)).thenReturn(List.of(testNewsletter));

        // Act
        ResponseEntity<List<Newsletter>> response = newsletterController.getNewslettersForJournalist(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(newsletterRepository).findByJournalistUserId(userId);
    }

    /**
     * Tests adding a journalist to a newsletter successfully.
     */
    @Test
    void addJournalistToNewsletter_Success() {
        // Arrange
        Long newsletterId = 1L;
        Long userId = 1L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(newsletterRepository.save(any(Newsletter.class))).thenReturn(testNewsletter);

        // Act
        ResponseEntity<?> response = newsletterController.addJournalistToNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Newsletter savedNewsletter = (Newsletter) response.getBody();
        assertTrue(savedNewsletter.getJournalists().contains(testUser));
        verify(newsletterRepository).save(any(Newsletter.class));
    }

    /**
     * Tests adding a journalist to a non-existent newsletter.
     */
    @Test
    void addJournalistToNewsletter_NewsletterNotFound() {
        // Arrange
        Long newsletterId = 999L;
        Long userId = 1L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.addJournalistToNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests adding a non-existent user as a journalist to a newsletter.
     */
    @Test
    void addJournalistToNewsletter_UserNotFound() {
        // Arrange
        Long newsletterId = 1L;
        Long userId = 999L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.addJournalistToNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("User with ID 999 not found.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests removing a journalist from a newsletter successfully.
     */
    @Test
    void removeJournalistFromNewsletter_Success() {
        // Arrange
        Long newsletterId = 1L;
        Long userId = 1L;
        testNewsletter.getJournalists().add(testUser);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(newsletterRepository.save(any(Newsletter.class))).thenReturn(testNewsletter);

        // Act
        ResponseEntity<?> response = newsletterController.removeJournalistFromNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Newsletter savedNewsletter = (Newsletter) response.getBody();
        assertFalse(savedNewsletter.getJournalists().contains(testUser));
        verify(newsletterRepository).save(any(Newsletter.class));
    }

    /**
     * Tests removing a journalist from a non-existent newsletter.
     */
    @Test
    void removeJournalistFromNewsletter_NewsletterNotFound() {
        // Arrange
        Long newsletterId = 999L;
        Long userId = 1L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.removeJournalistFromNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests removing a non-existent user as a journalist from a newsletter.
     */
    @Test
    void removeJournalistFromNewsletter_UserNotFound() {
        // Arrange
        Long newsletterId = 1L;
        Long userId = 999L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.removeJournalistFromNewsletter(newsletterId, userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("User with ID 999 not found.", body.get("message"));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests adding an article to a newsletter successfully.
     */
    @Test
    void addArticleToNewsletter_Success() {
        // Arrange
        Long newsletterId = 1L;
        Long articleId = 1L;
        Map<String, Object> articleData = new HashMap<>();
        articleData.put("articleId", articleId);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        // Act
        ResponseEntity<?> response = newsletterController.addArticleToNewsletter(newsletterId, articleData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Article added to the newsletter successfully.", body.get("message"));
        verify(articleRepository).save(any(Article.class));
    }

    /**
     * Tests adding an article to a newsletter when the newsletter is not found.
     */
    @Test
    void addArticleToNewsletter_NewsletterNotFound() {
        // Arrange
        Long newsletterId = 999L;
        Long articleId = 1L;
        Map<String, Object> articleData = new HashMap<>();
        articleData.put("articleId", articleId);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.addArticleToNewsletter(newsletterId, articleData);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(articleRepository, never()).save(any(Article.class));
    }

    /**
     * Tests adding an article to a newsletter when the article is not found.
     */
    @Test
    void addArticleToNewsletter_ArticleNotFound() {
        // Arrange
        Long newsletterId = 1L;
        Long articleId = 999L;
        Map<String, Object> articleData = new HashMap<>();
        articleData.put("articleId", articleId);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.addArticleToNewsletter(newsletterId, articleData);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Article with ID 999 not found.", body.get("message"));
        verify(articleRepository, never()).save(any(Article.class));
    }

    /**
     * Tests adding an article to a newsletter when the article already exists in the newsletter.
     */
    @Test
    void addArticleToNewsletter_ArticleAlreadyExists() {
        // Arrange
        Long newsletterId = 1L;
        Long articleId = 1L;
        Map<String, Object> articleData = new HashMap<>();
        articleData.put("articleId", articleId);

        testNewsletter.getArticles().add(testArticle);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));

        // Act
        ResponseEntity<?> response = newsletterController.addArticleToNewsletter(newsletterId, articleData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Article already exists in the newsletter.", body.get("message"));
        verify(articleRepository, never()).save(any(Article.class));
    }

    /**
     * Tests removing an article from a newsletter successfully.
     */
    @Test
    void removeArticleFromNewsletter_Success() {
        // Arrange
        Long newsletterId = 1L;
        Long articleId = 1L;

        testNewsletter.getArticles().add(testArticle);
        testArticle.setNewsletter(testNewsletter);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);
        when(newsletterRepository.save(any(Newsletter.class))).thenReturn(testNewsletter);

        // Act
        ResponseEntity<?> response = newsletterController.removeArticleFromNewsletter(newsletterId, articleId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Article successfully removed from the newsletter.", body.get("message"));
        verify(articleRepository).save(any(Article.class));
        verify(newsletterRepository).save(any(Newsletter.class));
    }

    /**
     * Tests removing an article from a newsletter when the newsletter is not found.
     */
    @Test
    void removeArticleFromNewsletter_NewsletterNotFound() {
        // Arrange
        Long newsletterId = 999L;
        Long articleId = 1L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.removeArticleFromNewsletter(newsletterId, articleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Newsletter with ID 999 not found.", body.get("message"));
        verify(articleRepository, never()).save(any(Article.class));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests removing an article from a newsletter when the article is not found.
     */
    @Test
    void removeArticleFromNewsletter_ArticleNotFound() {
        // Arrange
        Long newsletterId = 1L;
        Long articleId = 999L;

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = newsletterController.removeArticleFromNewsletter(newsletterId, articleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Article with ID 999 not found.", body.get("message"));
        verify(articleRepository, never()).save(any(Article.class));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }

    /**
     * Tests removing an article from a newsletter when the article does not belong to the newsletter.
     */
    @Test
    void removeArticleFromNewsletter_ArticleDoesNotBelong() {
        // Arrange
        Long newsletterId = 1L;
        Long articleId = 1L;

        // Article does not belong to the newsletter
        testArticle.setNewsletter(null);

        when(newsletterRepository.findById(newsletterId)).thenReturn(Optional.of(testNewsletter));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));

        // Act
        ResponseEntity<?> response = newsletterController.removeArticleFromNewsletter(newsletterId, articleId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Article does not belong to the specified newsletter.", body.get("message"));
        verify(articleRepository, never()).save(any(Article.class));
        verify(newsletterRepository, never()).save(any(Newsletter.class));
    }


}
