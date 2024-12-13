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

    // Injection du repository via le constructeur
    public NewsletterController(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    // CREATE: Ajouter une nouvelle newsletter
    @PostMapping
    public ResponseEntity<Newsletter> addNewsletter(@RequestBody Newsletter newNewsletter) {
        Newsletter savedNewsletter = newsletterRepository.save(newNewsletter);
        return ResponseEntity.ok(savedNewsletter);
    }

    // READ: Récupérer toutes les newsletters
    @GetMapping("/all")
    public ResponseEntity<List<Newsletter>> getAllNewsletters() {
        List<Newsletter> newsletters = newsletterRepository.findAll();
        return ResponseEntity.ok(newsletters);
    }

    // READ: Récupérer une newsletter par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Newsletter> getNewsletterById(@PathVariable Long id) {
        return newsletterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Mise à jour de la méthode updateNewsletter
    @PutMapping("/{id}")
    public ResponseEntity<Newsletter> updateNewsletter(@PathVariable Long id, @RequestBody Newsletter updatedNewsletter) {
        return newsletterRepository.findById(id)
                .map(existingNewsletter -> {
                    existingNewsletter.setTitle(updatedNewsletter.getTitle());
                    existingNewsletter.setSubtitle(updatedNewsletter.getSubtitle());
                    existingNewsletter.setPublicationDate(updatedNewsletter.getPublicationDate());
                    existingNewsletter.setRead(updatedNewsletter.isRead()); // Utilisation correcte
                    Newsletter savedNewsletter = newsletterRepository.save(existingNewsletter);
                    return ResponseEntity.ok(savedNewsletter);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // DELETE: Supprimer une newsletter par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNewsletter(@PathVariable Long id) {
        if (newsletterRepository.existsById(id)) {
            newsletterRepository.deleteById(id);
            return ResponseEntity.ok("Newsletter deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    // Exemple pour la méthode markAsRead
    @PatchMapping("/{id}/read")
    public ResponseEntity<Newsletter> markAsRead(@PathVariable Long id) {
        return newsletterRepository.findById(id)
                .map(existingNewsletter -> {
                    existingNewsletter.setRead(true); // Utilisation correcte
                    Newsletter updatedNewsletter = newsletterRepository.save(existingNewsletter);
                    return ResponseEntity.ok(updatedNewsletter);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
