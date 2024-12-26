package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Image;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class contains unit tests for the {@link ImageController}.
 * It uses Mockito to mock dependencies and JUnit 5 for testing.
 */
@SpringBootTest
class ImageControllerTest {

    /**
     * Mocked repository for images.
     */
    @Mock
    private ImageRepository imageRepository;

    /**
     * Mocked repository for articles.
     */
    @Mock
    private ArticleRepository articleRepository;

    /**
     * The ImageController instance to be tested.
     */
    @InjectMocks
    private ImageController imageController;

    /**
     * MockMvc for simulating HTTP requests.
     */
    private MockMvc mockMvc;

    /**
     * Test image object.
     */
    private Image testImage;

    /**
     * Test article object.
     */
    private Article testArticle;

    /**
     * Test image bytes.
     */
    private byte[] testImageBytes;

    /**
     * Sets up the test environment before each test method.
     * It initializes MockMvc and test data for images and articles.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();

        // Initialisation des données de test
        testArticle = new Article();
        testArticle.setArticleId(1L);

        testImageBytes = "test image content".getBytes();
        testImage = new Image();
        testImage.setImageId(1L);
        testImage.setImagePath(testImageBytes);
        testImage.setArticle(testArticle);
    }


    /**
     * Tests the successful addition of an image.
     */
    @Test
    void addImage_Success() {
        // Arrange
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imagePath", Base64.getEncoder().encodeToString(testImageBytes));
        imageData.put("articleId", 1L);

        when(imageRepository.save(any(Image.class))).thenReturn(testImage);

        // Act
        ResponseEntity<Image> response = imageController.addImage(imageData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    /**
     * Tests the successful retrieval of all images.
     */
    @Test
    void getAllImages_Success() {
        // Arrange
        List<Image> images = Arrays.asList(testImage);
        when(imageRepository.findAll()).thenReturn(images);

        // Act
        ResponseEntity<List<Image>> response = imageController.getAllImages();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(imageRepository, times(1)).findAll();
    }

    /**
     * Tests the successful retrieval of an image by its ID.
     */
    @Test
    void getImageById_Success() {
        // Arrange
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        ResponseEntity<Image> response = imageController.getImageById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(imageRepository, times(1)).findById(1L);
    }

    /**
     * Tests the scenario where an image is not found by its ID.
     */
    @Test
    void getImageById_NotFound() {
        // Arrange
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Image> response = imageController.getImageById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(imageRepository, times(1)).findById(999L);
    }

    /**
     * Tests the successful deletion of an image.
     */
    @Test
    void deleteImage_Success() {
        // Arrange
        when(imageRepository.existsById(1L)).thenReturn(true);
        doNothing().when(imageRepository).deleteById(1L);

        // Act
        ResponseEntity<String> response = imageController.deleteImage(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Image deleted successfully", response.getBody());
        verify(imageRepository, times(1)).deleteById(1L);
    }

    /**
     * Tests the scenario where deletion is attempted for an image ID that does not exist.
     */
    @Test
    void deleteImage_NotFound() {
        // Arrange
        when(imageRepository.existsById(999L)).thenReturn(false);

        // Act
        ResponseEntity<String> response = imageController.deleteImage(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(imageRepository, never()).deleteById(any());
    }

    /**
     * Tests the successful retrieval of an image as Base64 string by its ID.
     */
    @Test
    void getImageAsBase64_Success() {
        // Arrange
        when(imageRepository.findById(1L)).thenReturn(Optional.of(testImage));

        // Act
        ResponseEntity<String> response = imageController.getImageAsBase64(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(imageRepository, times(1)).findById(1L);
    }

    /**
     * Tests the successful retrieval of images by their associated article's ID.
     */
    @Test
    void getImagesByArticleId_Success() {
        // Arrange
        List<Image> images = Arrays.asList(testImage);
        when(imageRepository.findByArticleArticleId(1L)).thenReturn(images);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = imageController.getImagesByArticleId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(imageRepository, times(1)).findByArticleArticleId(1L);
    }

    /**
     * Tests the scenario where no images are found for a given article ID.
     */
    @Test
    void getImagesByArticleId_NotFound() {
        // Arrange
        when(imageRepository.findByArticleArticleId(999L)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Map<String, Object>>> response = imageController.getImagesByArticleId(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(imageRepository, times(1)).findByArticleArticleId(999L);
    }

    /**
     * Tests the successful update of an article.
     */
    @Test
    void updateArticle_Success() {
        // Arrange
        Article updatedArticle = new Article();
        updatedArticle.setArticleId(1L);
        updatedArticle.setTitle("Updated Title");
        updatedArticle.setImages(new ArrayList<>());

        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);

        // Act
        ResponseEntity<Article> response = imageController.updateArticle(1L, updatedArticle);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    /**
     * Tests the scenario where updating an article with a non-existent ID throws an exception.
     */
    @Test
    void updateArticle_NotFound() {
        // Arrange
        Article updatedArticle = new Article();
        when(articleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            imageController.updateArticle(999L, updatedArticle);
        });
        verify(articleRepository, times(1)).findById(999L);
        verify(articleRepository, never()).save(any(Article.class));
    }
}