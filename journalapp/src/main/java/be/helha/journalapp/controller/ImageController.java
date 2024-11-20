package be.helha.journalapp.controller;

import be.helha.journalapp.model.Image;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    private List<Image> images = new ArrayList<>(); // In-memory storage for images
    private Long currentId = 1L; // Counter for generating unique IDs

    // CREATE: Add a new image
    @PostMapping
    public Image addImage(@RequestBody Image newImage) {
        newImage.setImage_Id(currentId++); // Set a unique ID for the new image
        images.add(newImage); // Add the image to the list
        return newImage; // Return the created image
    }

    // READ: Retrieve all images
    @GetMapping
    public List<Image> getAllImages() {
        return images; // Return the list of images
    }

    // READ: Retrieve a specific image by its ID
    @GetMapping("/{id}")
    public Image getImageById(@PathVariable Long id) {
        return images.stream()
                .filter(image -> image.getImage_Id().equals(id)) // Find the image with the matching ID
                .findFirst()
                .orElse(null); // Return null if no image is found
    }

    // UPDATE: Update an existing image
    @PutMapping("/{id}")
    public Image updateImage(@PathVariable Long id, @RequestBody Image updatedImage) {
        for (Image image : images) {
            if (image.getImage_Id().equals(id)) { // Check if the ID matches
                image.setImage_Path(updatedImage.getImage_Path()); // Update the image data
                return image; // Return the updated image
            }
        }
        return null; // Return null if no image is found
    }

    // DELETE: Delete an image by its ID
    @DeleteMapping("/{id}")
    public String deleteImage(@PathVariable Long id) {
        boolean removed = images.removeIf(image -> image.getImage_Id().equals(id)); // Remove the image
        return removed ? "Image deleted successfully" : "Image not found"; // Return status message
    }

    // Additional: Get image as Base64 string (optional)
    @GetMapping("/{id}/base64")
    public String getImageAsBase64(@PathVariable Long id) {
        Image image = images.stream()
                .filter(img -> img.getImage_Id().equals(id))
                .findFirst()
                .orElse(null);
        if (image != null) {
            return Base64.getEncoder().encodeToString(image.getImage_Path()); // Return image as Base64 string
        }
        return null; // Return null if no image is found
    }
}
