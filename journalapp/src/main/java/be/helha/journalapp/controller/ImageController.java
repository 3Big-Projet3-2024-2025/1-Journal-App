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

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageRepository imageRepository;
    private final ArticleRepository articleRepository;

    // Constructor Injection
    public ImageController(ImageRepository imageRepository ,ArticleRepository articleRepository) {
        this.imageRepository = imageRepository;
        this.articleRepository = articleRepository;
    }

    // CREATE: Ajouter une nouvelle image
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


    // READ: Récupérer toutes les images
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageRepository.findAll();
        return ResponseEntity.ok(images);
    }

    // READ: Récupérer une image par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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



    // DELETE: Supprimer une image par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
            return ResponseEntity.ok("Image deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    // Extra: Obtenir une image en Base64
    @GetMapping("/{id}/base64")
    public ResponseEntity<String> getImageAsBase64(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> {
                    String base64 = Base64.getEncoder().encodeToString(image.getImagePath());
                    return ResponseEntity.ok(base64);
                })
                .orElse(ResponseEntity.notFound().build());
    }

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

