package be.helha.journalapp.controller;

import be.helha.journalapp.model.Newsletter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/newsletters")
public class NewsletterController {

    private List<Newsletter> newsletters = new ArrayList<>(); // In-memory storage for newsletters
    private Long currentId = 1L; // Counter for generating unique IDs

    // CREATE: Add a new newsletter
    @PostMapping
    public Newsletter addNewsletter(@RequestBody Newsletter newNewsletter) {
        newNewsletter.setNewsletter_Id(currentId++); // Set a unique ID for the new newsletter
        newsletters.add(newNewsletter); // Add the newsletter to the list
        return newNewsletter; // Return the created newsletter
    }

    // READ: Retrieve all newsletters
    @GetMapping
    public List<Newsletter> getAllNewsletters() {
        return newsletters; // Return the list of newsletters
    }

    // READ: Retrieve a specific newsletter by its ID
    @GetMapping("/{id}")
    public Newsletter getNewsletterById(@PathVariable Long id) {
        return newsletters.stream()
                .filter(newsletter -> newsletter.getNewsletter_Id().equals(id)) // Find the newsletter with the matching ID
                .findFirst()
                .orElse(null); // Return null if no newsletter is found
    }

    // UPDATE: Update an existing newsletter
    @PutMapping("/{id}")
    public Newsletter updateNewsletter(@PathVariable Long id, @RequestBody Newsletter updatedNewsletter) {
        for (Newsletter newsletter : newsletters) {
            if (newsletter.getNewsletter_Id().equals(id)) { // Check if the ID matches
                newsletter.setTitle(updatedNewsletter.getTitle()); // Update the title
                newsletter.setSubtitle(updatedNewsletter.getSubtitle()); // Update the subtitle
                newsletter.setPublicationDate(updatedNewsletter.getPublicationDate()); // Update the publication date
                newsletter.setIsRead(updatedNewsletter.isIsRead()); // Update validation status

                return newsletter; // Return the updated newsletter
            }
        }
        return null; // Return null if no newsletter is found
    }

    // DELETE: Delete a newsletter by its ID
    @DeleteMapping("/{id}")
    public String deleteNewsletter(@PathVariable Long id) {
        boolean removed = newsletters.removeIf(newsletter -> newsletter.getNewsletter_Id().equals(id)); // Remove the newsletter
        return removed ? "Newsletter deleted successfully" : "Newsletter not found"; // Return status message
    }

    // Additional: Mark a newsletter as read
    @PatchMapping("/{id}/read")
    public Newsletter markAsRead(@PathVariable Long id) {
        for (Newsletter newsletter : newsletters) {
            if (newsletter.getNewsletter_Id().equals(id)) { // Check if the ID matches
                newsletter.setIsRead(true); // Mark the newsletter as read
                return newsletter; // Return the updated newsletter
            }
        }
        return null; // Return null if no newsletter is found
    }
}
