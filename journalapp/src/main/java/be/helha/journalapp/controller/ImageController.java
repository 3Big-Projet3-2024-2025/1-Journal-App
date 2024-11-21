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

    // Injecting the repository via constructor
    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    // CREATE: Add a new image
    @PostMapping
    public ResponseEntity<Image> addImage(@RequestBody Image newImage) {
        Image savedImage = imageRepository.save(newImage); // Save the image to the database
        return ResponseEntity.ok(savedImage);
    }

    // READ: Retrieve all images
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageRepository.findAll(); // Fetch all images
        return ResponseEntity.ok(images);
    }

    // READ: Retrieve a specific image by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(ResponseEntity::ok) // Return the image if found
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // UPDATE: Update an existing image
    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable Long id, @RequestBody Image updatedImage) {
        return imageRepository.findById(id)
                .map(existingImage -> {
                    existingImage.setImage_Path(updatedImage.getImage_Path()); // Update image data
                    Image savedImage = imageRepository.save(existingImage); // Save the updated image
                    return ResponseEntity.ok(savedImage);
                })
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // DELETE: Delete an image by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id); // Delete the image by ID
            return ResponseEntity.ok("Image deleted successfully");
        }
        return ResponseEntity.notFound().build(); // Return 404 if not found
    }

    // Additional: Get an image as a Base64 string
    @GetMapping("/{id}/base64")
    public ResponseEntity<String> getImageAsBase64(@PathVariable Long id) {
        return imageRepository.findById(id)
                .map(image -> {
                    String base64 = Base64.getEncoder().encodeToString(image.getImage_Path()); // Convert to Base64
                    return ResponseEntity.ok(base64);
                })
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }
}
