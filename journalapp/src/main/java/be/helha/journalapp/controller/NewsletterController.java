package be.helha.journalapp.controller;

import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.repositories.NewsletterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newsletters")
public class NewsletterController {

    private final NewsletterRepository newsletterRepository;

    // Inject the repository via the constructor
    public NewsletterController(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    // CREATE: Add a new newsletter
    @PostMapping
    public ResponseEntity<Newsletter> addNewsletter(@RequestBody Newsletter newNewsletter) {
        Newsletter savedNewsletter = newsletterRepository.save(newNewsletter); // Save to the database
        return ResponseEntity.ok(savedNewsletter); // Return the created newsletter
    }

    // READ: Retrieve all newsletters
    @GetMapping
    public ResponseEntity<List<Newsletter>> getAllNewsletters() {
        List<Newsletter> newsletters = newsletterRepository.findAll(); // Fetch all newsletters from the database
        return ResponseEntity.ok(newsletters);
    }

    // READ: Retrieve a specific newsletter by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Newsletter> getNewsletterById(@PathVariable Long id) {
        return newsletterRepository.findById(id)
                .map(ResponseEntity::ok) // Return the found newsletter
                .orElse(ResponseEntity.notFound().build()); // Return 404 if not found
    }

    // UPDATE: Update an existing newsletter
    @PutMapping("/{id}")
    public ResponseEntity<Newsletter> updateNewsletter(@PathVariable Long id, @RequestBody Newsletter updatedNewsletter) {
        return newsletterRepository.findById(id)
                .map(existingNewsletter -> {
                    // Update fields
                    existingNewsletter.setTitle(updatedNewsletter.getTitle());
                    existingNewsletter.setSubtitle(updatedNewsletter.getSubtitle());
                    existingNewsletter.setPublicationDate(updatedNewsletter.getPublicationDate());
                    existingNewsletter.setIsRead(updatedNewsletter.isIsRead());
                    // Save updated newsletter
                    Newsletter savedNewsletter = newsletterRepository.save(existingNewsletter);
                    return ResponseEntity.ok(savedNewsletter);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Delete a newsletter by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNewsletter(@PathVariable Long id) {
        if (newsletterRepository.existsById(id)) {
            newsletterRepository.deleteById(id); // Delete from the database
            return ResponseEntity.ok("Newsletter deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Additional: Mark a newsletter as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Newsletter> markAsRead(@PathVariable Long id) {
        return newsletterRepository.findById(id)
                .map(existingNewsletter -> {
                    existingNewsletter.setIsRead(true); // Mark as read
                    Newsletter updatedNewsletter = newsletterRepository.save(existingNewsletter);
                    return ResponseEntity.ok(updatedNewsletter);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
