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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the {@link CommentController}.
 * It uses Mockito for mocking dependencies and JUnit 5 for testing.
 */
@SpringBootTest
class CommentControllerTest {

    /**
     * Mocked repository for comments.
     */
    @Mock
    private CommentRepository commentRepository;

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
     * The CommentController instance to be tested.
     */
    @InjectMocks
    private CommentController commentController;

    /**
     * MockMvc for simulating HTTP requests.
     */
    private MockMvc mockMvc;

    /**
     * Test comment object.
     */
    private Comment testComment;

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
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");

        testArticle = new Article();
        testArticle.setArticleId(1L);
        testArticle.setTitle("Test Article");

        testComment = new Comment();
        testComment.setCommentId(1L);
        testComment.setContent("Test Comment Content");
        testComment.setPublicationDate("2024-01-01");
        testComment.setUser(testUser);
        testComment.setArticle(testArticle);
    }

    /**
     * Tests the successful addition of a comment.
     */
    @Test
    void addComment_Success() {
        // Arrange
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("content", "Test Comment Content");
        commentData.put("publicationDate", "2024-01-01");
        commentData.put("user_id", 1L);
        commentData.put("article_id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        ResponseEntity<Comment> response = commentController.addComment(commentData);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(commentRepository).save(any(Comment.class));
    }

    /**
     * Tests the successful retrieval of all comments.
     */
    @Test
    void getAllComments_Success() {
        // Arrange
        when(commentRepository.findAll()).thenReturn(List.of(testComment));

        // Act
        ResponseEntity<List<Comment>> response = commentController.getAllComments();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(commentRepository).findAll();
    }

    /**
     * Tests the successful retrieval of a comment by its ID.
     */
    @Test
    void getCommentById_Success() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // Act
        ResponseEntity<Comment> response = commentController.getCommentById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(commentRepository).findById(1L);
    }

    /**
     * Tests the scenario where a comment is not found by its ID.
     */
    @Test
    void getCommentById_NotFound() {
        // Arrange
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Comment> response = commentController.getCommentById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(commentRepository).findById(999L);
    }

    /**
     * Tests the successful update of an existing comment.
     */
    @Test
    void updateComment_Success() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        Comment updatedComment = new Comment();
        updatedComment.setContent("Updated Content");
        updatedComment.setPublicationDate("2024-02-01");

        // Act
        ResponseEntity<Comment> response = commentController.updateComment(1L, updatedComment);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Content", response.getBody().getContent());
        verify(commentRepository).save(any(Comment.class));
    }

    /**
     * Tests the successful deletion of a comment.
     */
    @Test
    void deleteComment_Success() {
        // Arrange
        when(commentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(1L);

        // Act
        ResponseEntity<String> response = commentController.deleteComment(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully", response.getBody());
        verify(commentRepository).deleteById(1L);
    }

    /**
     * Tests the successful retrieval of comments by user ID.
     */
    @Test
    void getCommentsByUserId_Success() {
        // Arrange
        when(commentRepository.findByUserUserId(1L)).thenReturn(List.of(testComment));

        // Act
        ResponseEntity<List<Comment>> response = commentController.getCommentsByUserId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(commentRepository).findByUserUserId(1L);
    }

    /**
     * Tests the successful retrieval of comments by article ID.
     */
    @Test
    void getCommentsByArticleId_Success() {
        // Arrange
        when(commentRepository.findByArticleArticleId(1L)).thenReturn(List.of(testComment));

        // Act
        ResponseEntity<List<Comment>> response = commentController.getCommentsByArticleId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(commentRepository).findByArticleArticleId(1L);
    }
}
