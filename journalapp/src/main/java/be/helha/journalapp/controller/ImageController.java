package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Image;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.ImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * REST controller for managing image resources.
 * This controller handles requests related to adding, retrieving, updating, and deleting images.
 */
@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageRepository imageRepository;
    private final ArticleRepository articleRepository;

    /**
     * Constructor for ImageController, injecting dependencies.
     *
     * @param imageRepository The repository for accessing image data.
     * @param articleRepository The repository for accessing article data.
     */
    public ImageController(ImageRepository imageRepository ,ArticleRepository articleRepository) {
        this.imageRepository = imageRepository;
        this.articleRepository = articleRepository;
    }

    /**
     * Creates a new image.
     * Decodes the Base64 image, sets the associated article, and saves the image to the database.
     *
     * @param imageData A map containing the image data, including "imagePath" (Base64 encoded) and "articleId".
     * @return A ResponseEntity containing the saved Image object.
     */
    @PostMapping
    public ResponseEntity<Image> addImage(@RequestBody Map<String, Object> imageData) {
        Image image = new Image();

        // Décoder l'image Base64 en tableau d'octets
        String base64Image = (String) imageData.get("imagePath");
        byte[] decodedImage = Base64.getDecoder().decode(base64Image);
        image.setImagePath(decodedImage);

        // Associer l'article en utilisant l'ID
        Long articleId = ((Number) imageData.get("articleId")).longValue();
        Article article = new Article(); // Crée un article simplifié pour l'association
        article.setArticleId(articleId);
        image.setArticle(article);

        // Sauvegarde de l'image
        Image savedImage = imageRepository.save(image);
        return ResponseEntity.ok(savedImage);
    }


    /**
     * Retrieves all images from the database.
     *
     * @return A ResponseEntity containing a list of all Image objects.
     */
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageRepository.findAll();
        return ResponseEntity.ok(images);
    }

    /**
     * Retrieves an image by its ID.
     *
     * @param id The ID of the image to retrieve.
     * @return A ResponseEntity containing the Image object if found, or a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing article, synchronizing its associated images.
     * This method supports adding new images, updating existing images and removing images
     *
     * @param id           The ID of the article to update.
     * @param updatedArticle The updated Article object.
     * @return A ResponseEntity containing the updated Article object.
     * @throws RuntimeException If the article to update is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article updatedArticle) {
        // Charger l'article existant
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Mettre à jour les champs simples
        existingArticle.setTitle(updatedArticle.getTitle());
        existingArticle.setContent(updatedArticle.getContent());
        existingArticle.setPublicationDate(updatedArticle.getPublicationDate());
        existingArticle.setLongitude(updatedArticle.getLongitude());
        existingArticle.setLatitude(updatedArticle.getLatitude());
        existingArticle.setValid(updatedArticle.isValid());

        // Synchroniser les images
        // Supprimer les images qui ne sont plus présentes dans updatedArticle
        existingArticle.getImages().removeIf(image ->
                updatedArticle.getImages().stream()
                        .noneMatch(updatedImage -> updatedImage.getImageId().equals(image.getImageId()))
        );

        // Ajouter ou mettre à jour les images
        for (Image updatedImage : updatedArticle.getImages()) {
            if (updatedImage.getImageId() == null || updatedImage.getImageId() == 0) {
                // Nouvelle image
                existingArticle.addImage(updatedImage);
            } else {
                // Image existante : mettre à jour ses champs
                existingArticle.getImages().stream()
                        .filter(image -> image.getImageId().equals(updatedImage.getImageId()))
                        .findFirst()
                        .ifPresent(image -> image.setImagePath(updatedImage.getImagePath()));
            }
        }

        // Sauvegarder l'article mis à jour
        Article savedArticle = articleRepository.save(existingArticle);
        return ResponseEntity.ok(savedArticle);
    }



    /**
     * Deletes an image by its ID.
     *
     * @param id The ID of the image to delete.
     * @return A ResponseEntity with a success message if deleted, or a 404 Not Found response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
            return ResponseEntity.ok("Image deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves an image by its ID and returns it as a Base64 encoded string.
     *
     * @param id The ID of the image to retrieve.
     * @return A ResponseEntity containing the Base64 encoded image if found, or a 404 Not Found response.
     */
    @GetMapping("/{id}/base64")
    public ResponseEntity<String> getImageAsBase64(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> {
                    String base64 = Base64.getEncoder().encodeToString(image.getImagePath());
                    return ResponseEntity.ok(base64);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all images associated with a specific article ID.
     *
     * @param articleId The ID of the article to retrieve images for.
     * @return A ResponseEntity containing a list of image data (including Base64 encoded image paths) if found, or a 404 Not Found response.
     */
    @GetMapping("/article/{articleId}")
    @Transactional
    public ResponseEntity<List<Map<String, Object>>> getImagesByArticleId(@PathVariable Long articleId) {
        List<Image> images = imageRepository.findByArticleArticleId(articleId);
        if (images.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Transformez les données pour inclure les images en Base64
        List<Map<String, Object>> response = images.stream().map(image -> {
            Map<String, Object> imageData = new HashMap<>();
            imageData.put("imageId", image.getImageId());
            imageData.put("imagePath", Base64.getEncoder().encodeToString(image.getImagePath()));
            imageData.put("articleId", image.getArticle().getArticleId());
            return imageData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }



}

