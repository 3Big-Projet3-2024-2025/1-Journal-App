package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.NewsletterRepository;
import be.helha.journalapp.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/newsletters")
public class NewsletterController {

    private final NewsletterRepository newsletterRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    // Injection du repository via le constructeur
    public NewsletterController(NewsletterRepository newsletterRepository , UserRepository userRepository , ArticleRepository articleRepository) {
        this.newsletterRepository = newsletterRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    // CREATE: Ajouter une nouvelle newsletter
    @PostMapping
    public ResponseEntity<Newsletter> addNewsletter(@RequestBody Map<String, Object> newsletterData) {
        System.out.println("Données reçues : " + newsletterData);

        // Créer une instance de Newsletter
        Newsletter newsletter = new Newsletter();
        newsletter.setTitle((String) newsletterData.get("title"));
        newsletter.setSubtitle((String) newsletterData.get("subtitle"));
        newsletter.setPublicationDate((String) newsletterData.get("publicationDate"));

        // Gestion des nouvelles propriétés
        newsletter.setBackgroundColor((String) newsletterData.get("backgroundColor"));
        newsletter.setTitleFont((String) newsletterData.get("titleFont"));
        newsletter.setTitleFontSize((Integer) newsletterData.get("titleFontSize"));
        newsletter.setTitleColor((String) newsletterData.get("titleColor"));
        newsletter.setTitleBold((Boolean) newsletterData.get("titleBold"));
        newsletter.setTitleUnderline((Boolean) newsletterData.get("titleUnderline"));
        newsletter.setSubtitleFont((String) newsletterData.get("subtitleFont"));
        newsletter.setSubtitleFontSize((Integer) newsletterData.get("subtitleFontSize"));
        newsletter.setSubtitleColor((String) newsletterData.get("subtitleColor"));
        newsletter.setSubtitleBold((Boolean) newsletterData.get("subtitleBold"));
        newsletter.setSubtitleItalic((Boolean) newsletterData.get("subtitleItalic"));
        newsletter.setTextAlign((String) newsletterData.get("textAlign"));

        // Vérifie que l'ID du créateur est présent
        if (!newsletterData.containsKey("creator")) {
            throw new RuntimeException("Creator ID is missing from request.");
        }
        Long creatorId = ((Number) newsletterData.get("creator")).longValue();
        // Recherche de l'utilisateur
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User with ID " + creatorId + " not found."));
        // Associe l'utilisateur comme créateur
        newsletter.setCreator(creator);

        // Sauvegarde de la newsletter
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

    @PutMapping("/{id}")
    public ResponseEntity<Newsletter> updateNewsletter(@PathVariable Long id, @RequestBody Newsletter updatedNewsletter) {
        return newsletterRepository.findById(id)
                .map(existingNewsletter -> {
                    // Sauvegarde l'ancienne couleur de fond
                    String oldBackgroundColor = existingNewsletter.getBackgroundColor();

                    // Mise à jour des propriétés de la newsletter
                    existingNewsletter.setTitle(updatedNewsletter.getTitle());
                    existingNewsletter.setSubtitle(updatedNewsletter.getSubtitle());
                    existingNewsletter.setPublicationDate(updatedNewsletter.getPublicationDate());
                    existingNewsletter.setBackgroundColor(updatedNewsletter.getBackgroundColor());
                    existingNewsletter.setTitleFont(updatedNewsletter.getTitleFont());
                    existingNewsletter.setTitleFontSize(updatedNewsletter.getTitleFontSize());
                    existingNewsletter.setTitleColor(updatedNewsletter.getTitleColor());
                    existingNewsletter.setTitleBold(updatedNewsletter.isTitleBold());
                    existingNewsletter.setTitleUnderline(updatedNewsletter.isTitleUnderline());
                    existingNewsletter.setSubtitleFont(updatedNewsletter.getSubtitleFont());
                    existingNewsletter.setSubtitleFontSize(updatedNewsletter.getSubtitleFontSize());
                    existingNewsletter.setSubtitleColor(updatedNewsletter.getSubtitleColor());
                    existingNewsletter.setSubtitleBold(updatedNewsletter.isSubtitleBold());
                    existingNewsletter.setSubtitleItalic(updatedNewsletter.isSubtitleItalic());
                    existingNewsletter.setTextAlign(updatedNewsletter.getTextAlign());

                    // Sauvegarde la newsletter mise à jour
                    Newsletter savedNewsletter = newsletterRepository.save(existingNewsletter);

                    // Si la couleur de fond a changé, met à jour tous les articles associés
                    if (!oldBackgroundColor.equals(updatedNewsletter.getBackgroundColor())) {
                        updateArticlesBackgroundColor(savedNewsletter.getNewsletterId(), updatedNewsletter.getBackgroundColor());
                    }

                    return ResponseEntity.ok(savedNewsletter);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void updateArticlesBackgroundColor(Long newsletterId, String newBackgroundColor) {
        List<Article> articles = articleRepository.findByNewsletterNewsletterId(newsletterId);
        for (Article article : articles) {
            article.setBackgroundColor(newBackgroundColor);
        }
        articleRepository.saveAll(articles); // Sauvegarde en masse
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

    @GetMapping("/{newsletterId}/journalists/{userId}")
    public ResponseEntity<Boolean> isJournalistInNewsletter(@PathVariable Long newsletterId, @PathVariable Long userId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new RuntimeException("Newsletter with ID " + newsletterId + " not found."));
        boolean isJournalist = newsletter.getJournalists() != null && newsletter.getJournalists().stream()
                .anyMatch(user -> user.getUserId().equals(userId));
        return ResponseEntity.ok(isJournalist);
    }

    // Endpoint pour récupérer toutes les newsletters auxquelles un journaliste (userId) est associé
    @GetMapping("/journalist/{userId}")
    public ResponseEntity<List<Newsletter>> getNewslettersForJournalist(@PathVariable Long userId) {
        List<Newsletter> newsletters = newsletterRepository.findByJournalistUserId(userId);
        return ResponseEntity.ok(newsletters);
    }

    // Ajoute un journaliste à la liste de la newsletter
    @PatchMapping("/{newsletterId}/addJournalist/{userId}")
    public ResponseEntity<Newsletter> addJournalistToNewsletter(@PathVariable Long newsletterId, @PathVariable Long userId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new RuntimeException("Newsletter with ID " + newsletterId + " not found."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));

        List<User> journalists = newsletter.getJournalists();
        if (!journalists.contains(user)) {
            journalists.add(user);
            newsletter.setJournalists(journalists);
            newsletter = newsletterRepository.save(newsletter);
        }
        return ResponseEntity.ok(newsletter);
    }

    // Supprime un journaliste de la liste de la newsletter
    @PatchMapping("/{newsletterId}/removeJournalist/{userId}")
    public ResponseEntity<Newsletter> removeJournalistFromNewsletter(@PathVariable Long newsletterId, @PathVariable Long userId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new RuntimeException("Newsletter with ID " + newsletterId + " not found."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));

        List<User> journalists = newsletter.getJournalists();
        if (journalists.contains(user)) {
            journalists.remove(user);
            newsletter.setJournalists(journalists);
            newsletter = newsletterRepository.save(newsletter);
        }
        return ResponseEntity.ok(newsletter);
    }
}
