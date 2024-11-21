package be.helha.journalapp.controller;

import be.helha.journalapp.model.Image;
import be.helha.journalapp.repositories.ImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageRepository imageRepository;

    // Injection via le constructeur
    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // CREATE: Ajouter une nouvelle image
    @PostMapping
    public ResponseEntity<Image> addImage(@RequestBody Image newImage) {
        Image savedImage = imageRepository.save(newImage);
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

    // UPDATE: Mettre à jour une image existante
    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable Long id, @RequestBody Image updatedImage) {
        return imageRepository.findById(id)
                .map(existingImage -> {
                    existingImage.setImagePath(updatedImage.getImagePath()); // Mise à jour des données de l'image
                    Image savedImage = imageRepository.save(existingImage);
                    return ResponseEntity.ok(savedImage);
                })
                .orElse(ResponseEntity.notFound().build());
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
}
