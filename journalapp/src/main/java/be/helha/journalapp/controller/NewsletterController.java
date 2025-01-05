package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.Role;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.NewsletterRepository;
import be.helha.journalapp.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import be.helha.journalapp.repositories.RoleRepository;

/**
 * REST controller for managing newsletters.
 * <p>
 * Ce contrôleur fournit des points d'accès pour créer, lire, mettre à jour et supprimer des newsletters.
 * Il permet également de gérer les associations entre les newsletters, les journalistes (utilisateurs) et les articles.
 * </p>
 */
@RestController
@RequestMapping("/newsletters")
public class NewsletterController {

    private final NewsletterRepository newsletterRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final RoleRepository roleRepository;

    /**
     * Construit un nouveau NewsletterController avec les dépôts spécifiés.
     *
     * @param newsletterRepository le dépôt pour les newsletters
     * @param userRepository       le dépôt pour les utilisateurs
     * @param articleRepository    le dépôt pour les articles
     */
    public NewsletterController(NewsletterRepository newsletterRepository, UserRepository userRepository, ArticleRepository articleRepository, RoleRepository roleRepository) {
        this.newsletterRepository = newsletterRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Crée une nouvelle newsletter.
     *
     * @param newsletterData une carte contenant les données de la newsletter, y compris l'ID du créateur et d'autres attributs
     * @return un ResponseEntity contenant la newsletter créée ou un message d'erreur
     */
    @PostMapping
    public ResponseEntity<?> addNewsletter(@RequestBody Map<String, Object> newsletterData) {
        // Vérification des champs requis
        if (!newsletterData.containsKey("creator")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Creator ID is missing from request."));
        }

        Long creatorId;
        try {
            creatorId = ((Number) newsletterData.get("creator")).longValue();
        } catch (ClassCastException | NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid creator ID format."));
        }

        // Récupération de l'utilisateur créateur
        User creator = userRepository.findById(creatorId).orElse(null);
        if (creator == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User with ID " + creatorId + " not found."));
        }

        // Création et sauvegarde de la newsletter
        Newsletter newsletter = new Newsletter();
        newsletter.setTitle((String) newsletterData.get("title"));
        newsletter.setSubtitle((String) newsletterData.get("subtitle"));
        newsletter.setPublicationDate((String) newsletterData.get("publicationDate"));
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
        newsletter.setCreator(creator);

        Newsletter savedNewsletter = newsletterRepository.save(newsletter);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNewsletter);
    }

    /**
     * Récupère toutes les newsletters.
     *
     * @return un ResponseEntity contenant une liste de toutes les newsletters
     */
    @GetMapping("/all")
    public ResponseEntity<List<Newsletter>> getAllNewsletters() {
        List<Newsletter> newsletters = newsletterRepository.findAll();
        return ResponseEntity.ok(newsletters);
    }

    /**
     * Récupère une newsletter spécifique par son ID.
     *
     * @param id l'ID de la newsletter à récupérer
     * @return un ResponseEntity contenant la newsletter si trouvée, ou un message d'erreur si non trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsletterById(@PathVariable Long id) {
        Optional<Newsletter> newsletterOpt = newsletterRepository.findById(id);
        if (newsletterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + id + " not found."));
        }
        return ResponseEntity.ok(newsletterOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNewsletter(@PathVariable Long id, @RequestBody Map<String, Object> updatedNewsletterData) {
        Optional<Newsletter> existingOpt = newsletterRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + id + " not found."));
        }

        Newsletter existingNewsletter = existingOpt.get();

        // Mettre à jour les champs
        existingNewsletter.setTitle((String) updatedNewsletterData.get("title"));
        existingNewsletter.setSubtitle((String) updatedNewsletterData.get("subtitle"));
        existingNewsletter.setPublicationDate((String) updatedNewsletterData.get("publicationDate"));
        existingNewsletter.setBackgroundColor((String) updatedNewsletterData.get("backgroundColor"));
        existingNewsletter.setTitleFont((String) updatedNewsletterData.get("titleFont"));
        existingNewsletter.setTitleFontSize((Integer) updatedNewsletterData.get("titleFontSize"));
        existingNewsletter.setTitleColor((String) updatedNewsletterData.get("titleColor"));
        existingNewsletter.setTitleBold((Boolean) updatedNewsletterData.get("titleBold"));
        existingNewsletter.setTitleUnderline((Boolean) updatedNewsletterData.get("titleUnderline"));
        existingNewsletter.setSubtitleFont((String) updatedNewsletterData.get("subtitleFont"));
        existingNewsletter.setSubtitleFontSize((Integer) updatedNewsletterData.get("subtitleFontSize"));
        existingNewsletter.setSubtitleColor((String) updatedNewsletterData.get("subtitleColor"));
        existingNewsletter.setSubtitleBold((Boolean) updatedNewsletterData.get("subtitleBold"));
        existingNewsletter.setSubtitleItalic((Boolean) updatedNewsletterData.get("subtitleItalic"));
        existingNewsletter.setTextAlign((String) updatedNewsletterData.get("textAlign"));

        // Gérer le créateur
        if (updatedNewsletterData.containsKey("creator")) {
            Long creatorId;
            try {
                creatorId = ((Number) updatedNewsletterData.get("creator")).longValue();
            } catch (ClassCastException | NullPointerException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid creator ID format."));
            }

            User creator = userRepository.findById(creatorId).orElse(null);
            if (creator == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User with ID " + creatorId + " not found."));
            }
            existingNewsletter.setCreator(creator);
        }

        Newsletter savedNewsletter = newsletterRepository.save(existingNewsletter);

        // Mettre à jour les articles si nécessaire
        String oldBackgroundColor = existingNewsletter.getBackgroundColor();
        if (oldBackgroundColor != null && !oldBackgroundColor.equals(existingNewsletter.getBackgroundColor())) {
            updateArticlesBackgroundColor(savedNewsletter.getNewsletterId(), existingNewsletter.getBackgroundColor());
        }

        return ResponseEntity.ok(savedNewsletter);
    }


    /**
     * Met à jour la couleur de fond de tous les articles associés à une newsletter.
     *
     * @param newsletterId        l'ID de la newsletter
     * @param newBackgroundColor la nouvelle couleur de fond à appliquer
     */
    private void updateArticlesBackgroundColor(Long newsletterId, String newBackgroundColor) {
        List<Article> articles = articleRepository.findByNewsletterNewsletterId(newsletterId);
        for (Article article : articles) {
            article.setBackgroundColor(newBackgroundColor);
        }
        articleRepository.saveAll(articles);
    }

    /**
     * Supprime une newsletter par son ID.
     *
     * @param id l'ID de la newsletter à supprimer
     * @return un ResponseEntity indiquant le résultat de l'opération de suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNewsletter(@PathVariable Long id) {
        if (!newsletterRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + id + " not found."));
        }
        newsletterRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Newsletter deleted successfully"));
    }

    /**
     * Vérifie si un utilisateur spécifique est un journaliste associé à une newsletter donnée.
     *
     * @param newsletterId l'ID de la newsletter
     * @param userId       l'ID de l'utilisateur (journaliste) à vérifier
     * @return un ResponseEntity contenant un booléen indiquant si l'utilisateur est un journaliste dans la newsletter
     */
    @GetMapping("/{newsletterId}/journalists/{userId}")
    public ResponseEntity<?> isJournalistInNewsletter(@PathVariable Long newsletterId, @PathVariable Long userId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElse(null);
        if (newsletter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + newsletterId + " not found."));
        }
        boolean isJournalist = newsletter.getJournalists() != null && newsletter.getJournalists().stream()
                .anyMatch(user -> user.getUserId().equals(userId));
        return ResponseEntity.ok(Map.of("isJournalist", isJournalist));
    }

    /**
     * Récupère toutes les newsletters associées à un journaliste spécifique (userId).
     *
     * @param userId l'ID du journaliste (utilisateur)
     * @return un ResponseEntity contenant une liste de newsletters associées au journaliste
     */
    @GetMapping("/journalist/{userId}")
    public ResponseEntity<List<Newsletter>> getNewslettersForJournalist(@PathVariable Long userId) {
        List<Newsletter> newsletters = newsletterRepository.findByJournalistUserId(userId);
        return ResponseEntity.ok(newsletters);
    }

    /**
     * Ajoute un journaliste (utilisateur) à la liste des journalistes d'une newsletter.
     *
     * @param newsletterId l'ID de la newsletter
     * @param userId       l'ID de l'utilisateur (journaliste) à ajouter
     * @return un ResponseEntity contenant la newsletter mise à jour ou un message d'erreur
     */
    @PatchMapping("/{newsletterId}/addJournalist/{userId}")
    public ResponseEntity<?> addJournalistToNewsletter(@PathVariable Long newsletterId, @PathVariable Long userId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElse(null);
        if (newsletter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + newsletterId + " not found."));
        }
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User with ID " + userId + " not found."));
        }

        List<User> journalists = newsletter.getJournalists();
        if (!journalists.contains(user)) {
            journalists.add(user);
            newsletter.setJournalists(journalists);
            newsletter = newsletterRepository.save(newsletter);
        }
        return ResponseEntity.ok(newsletter);
    }

    @PostMapping("/{newsletterId}/addJournalistByEmail")
    @Transactional
    public ResponseEntity<?> addJournalistByEmail(
            @PathVariable Long newsletterId,
            @RequestBody Map<String, String> body) {

        // Récupération de l'email du corps de la requête
        String email = body.get("email");

        // Vérification que l'email est présent
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email is required."));
        }

        // Recherche de l'utilisateur par email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User with email " + email + " not found."));
        }

        User user = userOpt.get();

        // Recherche de la newsletter par ID
        Optional<Newsletter> newsletterOpt = newsletterRepository.findById(newsletterId);
        if (newsletterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Newsletter with ID " + newsletterId + " not found."));
        }

        Newsletter newsletter = newsletterOpt.get();

        // Vérifie si l'utilisateur est déjà un journaliste de la newsletter
        if (newsletter.getJournalists().contains(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "User is already a journalist for this newsletter."));
        }

        // Ajouter l'utilisateur à la liste des journalistes
        newsletter.getJournalists().add(user);

        // Mise à jour du rôle de l'utilisateur en "JOURNALIST"
        Optional<Role> journalistRoleOpt = roleRepository.findByRoleName("JOURNALIST");
        if (journalistRoleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Role 'JOURNALIST' not found in the database."));
        }

        Role journalistRole = journalistRoleOpt.get();
        if (!user.getRole().getRoleName().equals("JOURNALIST")) {
            user.setRole(journalistRole); // Change le rôle de l'utilisateur
            userRepository.save(user); // Sauvegarde l'utilisateur avec le nouveau rôle
        }

        // Sauvegarde de la newsletter mise à jour
        newsletterRepository.save(newsletter);

        return ResponseEntity.ok(Map.of("message", "Journalist added successfully and role updated."));
    }



    /**
     * Supprime un journaliste (utilisateur) de la liste des journalistes d'une newsletter.
     *
     * @param newsletterId l'ID de la newsletter
     * @param userId       l'ID de l'utilisateur (journaliste) à supprimer
     * @return un ResponseEntity contenant la newsletter mise à jour ou un message d'erreur
     */
    @PatchMapping("/{newsletterId}/removeJournalist/{userId}")
    public ResponseEntity<?> removeJournalistFromNewsletter(@PathVariable Long newsletterId, @PathVariable Long userId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElse(null);
        if (newsletter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + newsletterId + " not found."));
        }
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User with ID " + userId + " not found."));
        }

        List<User> journalists = newsletter.getJournalists();
        if (journalists.contains(user)) {
            journalists.remove(user);
            newsletter.setJournalists(journalists);
            newsletter = newsletterRepository.save(newsletter);
        }
        return ResponseEntity.ok(newsletter);
    }

    /**
     * Ajoute un article à une newsletter spécifique.
     *
     * @param newsletterId l'ID de la newsletter
     * @param articleData  une carte contenant l'ID de l'article à ajouter
     * @return un ResponseEntity indiquant le résultat de l'opération ou un message d'erreur
     */
    @PostMapping("/{newsletterId}/addArticle")
    public ResponseEntity<?> addArticleToNewsletter(@PathVariable Long newsletterId, @RequestBody Map<String, Object> articleData) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElse(null);
        if (newsletter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + newsletterId + " not found."));
        }

        if (!articleData.containsKey("articleId")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Article ID is missing from the request."));
        }

        Long articleId;
        try {
            articleId = ((Number) articleData.get("articleId")).longValue();
        } catch (ClassCastException | NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid article ID format."));
        }

        Article article = articleRepository.findById(articleId)
                .orElse(null);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Article with ID " + articleId + " not found."));
        }

        boolean exists = newsletter.getArticles().stream()
                .anyMatch(existingArticle -> existingArticle.getArticleId().equals(articleId));

        if (exists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Article already exists in the newsletter."));
        }

        article.setNewsletter(newsletter);
        articleRepository.save(article);

        return ResponseEntity.ok(Map.of("message", "Article added to the newsletter successfully."));
    }

    /**
     * Supprime un article d'une newsletter spécifique.
     *
     * @param newsletterId l'ID de la newsletter
     * @param articleId    l'ID de l'article à supprimer
     * @return un ResponseEntity indiquant le résultat de l'opération ou un message d'erreur
     */
    @DeleteMapping("/{newsletterId}/removeArticle/{articleId}")
    public ResponseEntity<?> removeArticleFromNewsletter(@PathVariable Long newsletterId, @PathVariable Long articleId) {
        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElse(null);
        if (newsletter == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Newsletter with ID " + newsletterId + " not found."));
        }

        Article article = articleRepository.findById(articleId)
                .orElse(null);
        if (article == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Article with ID " + articleId + " not found."));
        }

        if (article.getNewsletter() == null || !article.getNewsletter().getNewsletterId().equals(newsletterId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Article does not belong to the specified newsletter."));
        }

        newsletter.getArticles().remove(article);
        article.setNewsletter(null);
        articleRepository.save(article);
        newsletterRepository.save(newsletter);

        return ResponseEntity.ok(Map.of("message", "Article successfully removed from the newsletter."));
    }
    @GetMapping("/by-editor-email/{email}")
    public ResponseEntity<List<Newsletter>> getNewslettersByEditorEmail(@PathVariable String email) {
        User editor = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Éditeur non trouvé avec cet email : " + email));

        List<Newsletter> newsletters = newsletterRepository.findByCreator(editor);
        return ResponseEntity.ok(newsletters);
    }

}
