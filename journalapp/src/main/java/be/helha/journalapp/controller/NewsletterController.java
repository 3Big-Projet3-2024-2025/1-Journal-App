package be.helha.journalapp.controller;



import be.helha.journalapp.DTONEWS.Newsletterdto;
import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.NewsletterRepository;
import be.helha.journalapp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newsletters")
public class NewsletterController {

    private final NewsletterRepository newsletterRepository;
    private final UserRepository userRepository;

    // Injection via le constructeur
    public NewsletterController(NewsletterRepository newsletterRepository, UserRepository userRepository) {
        this.newsletterRepository = newsletterRepository;
        this.userRepository = userRepository;
    }

    // CREATE: Ajouter une nouvelle newsletter avec un DTO
    @PostMapping
    public ResponseEntity<Newsletter> addNewsletter(@RequestBody Newsletterdto dto) {
        // Récupérer le User existant à partir de l'ID fourni

        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Créer la newsletter et l’associer au créateur
        Newsletter newsletter = new Newsletter();
        newsletter.setTitle(dto.getTitle());
        newsletter.setSubtitle(dto.getSubtitle());
        newsletter.setPublicationDate(dto.getPublicationDate());
        newsletter.setRead(false);
        newsletter.setCreator(creator);

        // Sauvegarder
        Newsletter savedNewsletter = newsletterRepository.save(newsletter);
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

    // UPDATE: Mettre à jour une newsletter existante par son ID
    @PutMapping("/{id}")
    public ResponseEntity<Newsletter> updateNewsletter(@PathVariable Long id, @RequestBody Newsletter updatedNewsletter) {
        return newsletterRepository.findById(id)
                .map(existingNewsletter -> {
                    existingNewsletter.setTitle(updatedNewsletter.getTitle());
                    existingNewsletter.setSubtitle(updatedNewsletter.getSubtitle());
                    existingNewsletter.setPublicationDate(updatedNewsletter.getPublicationDate());
                    existingNewsletter.setRead(updatedNewsletter.isRead());
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

    // PATCH: Marquer une newsletter comme lue
    @PatchMapping("/{id}/read")
    public ResponseEntity<Newsletter> markAsRead(@PathVariable Long id) {
        return newsletterRepository.findById(id)
                .map(existingNewsletter -> {
                    existingNewsletter.setRead(true);
                    Newsletter updatedNewsletter = newsletterRepository.save(existingNewsletter);
                    return ResponseEntity.ok(updatedNewsletter);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
