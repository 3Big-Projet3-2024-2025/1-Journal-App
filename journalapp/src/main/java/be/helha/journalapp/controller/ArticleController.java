package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.User;
import be.helha.journalapp.model.UserArticleRead;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.NewsletterRepository;
import be.helha.journalapp.repositories.UserArticleReadRepository;
import be.helha.journalapp.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing articles.
 * This controller handles requests related to adding, retrieving, updating, deleting, and validating articles,
 * as well as marking articles as read and retrieving read statuses.
 */
@RestController
@RequestMapping("/articles")
public class ArticleController {


    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NewsletterRepository newsletterRepository;
    private final UserArticleReadRepository userArticleReadRepository;

    /**
     * Constructor for ArticleController, injecting dependencies.
     * @param articleRepository The repository for accessing article data.
     * @param userRepository The repository for accessing user data.
     * @param newsletterRepository The repository for accessing newsletter data.
     * @param userArticleReadRepository The repository for accessing user article read data.
     */
    public ArticleController(ArticleRepository articleRepository, UserRepository userRepository, NewsletterRepository newsletterRepository, UserArticleReadRepository userArticleReadRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.newsletterRepository = newsletterRepository;
        this.userArticleReadRepository = userArticleReadRepository;
    }


    /**
     * Creates a new article.
     * Sets the title, content, publication date, longitude, latitude, valid status, and associates the article with a newsletter and an author.
     * Also sets the background color of the article based on the newsletter's background color.
     *
     * @param articleData A map containing the article data.
     * @return A ResponseEntity containing the saved Article object.
     * @throws RuntimeException if the Newsletter or User are not found
     */
    @PostMapping
    public ResponseEntity<Article> addArticle(@RequestBody Map<String, Object> articleData) {
        Article article = new Article();
        article.setTitle((String) articleData.get("title"));
        article.setContent((String) articleData.get("content"));
        article.setPublicationDate((String) articleData.get("publicationDate"));
        article.setLongitude((Double) articleData.get("longitude"));
        article.setLatitude((Double) articleData.get("latitude"));
        article.setValid((Boolean) articleData.get("valid"));
        article.setRead((Boolean) articleData.getOrDefault("read", false)); // Défaut: non lu

        Long newsletterId = ((Number) articleData.get("newsletter_id")).longValue();
        Long userId = ((Number) articleData.get("user_id")).longValue();

        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new RuntimeException("Newsletter not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        article.setNewsletter(newsletter);
        article.setAuthor(author);

        // Définir la couleur de fond basée sur la newsletter
        article.setBackgroundColor(newsletter.getBackgroundColor());

        Article savedArticle = articleRepository.save(article);
        return ResponseEntity.ok(savedArticle);
    }



    /**
     * Retrieves the background color of a newsletter by its ID.
     *
     * @param newsletterId The ID of the newsletter.
     * @return A ResponseEntity containing a map with the background color if found, or a 404 Not Found response.
     */
    @GetMapping("/{newsletterId}/background-color")
    public ResponseEntity<Map<String, String>> getNewsletterBackgroundColor(@PathVariable Long newsletterId) {
        Optional<Newsletter> newsletter = newsletterRepository.findById(newsletterId);

        if (newsletter.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("backgroundColor", newsletter.get().getBackgroundColor());
            return ResponseEntity.ok(response);
        }
        // Newsletter non trouvée
        return ResponseEntity.status(404).body(Map.of("error", "Newsletter not found"));
    }

    /**
     * Retrieves all articles.
     *
     * @return A ResponseEntity containing a list of all Article objects.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return ResponseEntity.ok(articles);
    }

    /**
     * Retrieves all articles belonging to newsletters created by a specific editor.
     *
     * @param editorId The ID of the editor.
     * @return A ResponseEntity containing a list of articles or a 204 No Content response if none are found.
     */
    @GetMapping("/by-editor/{editorId}")
    public ResponseEntity<List<Article>> getArticlesByEditorId(@PathVariable Long editorId) {
        List<Article> articles = articleRepository.findArticlesByEditorId(editorId);
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content if no articles found
        }
        return ResponseEntity.ok(articles); // Return the list of articles
    }


    /**
     * Retrieves an article by its ID.
     * @param id The ID of the article to retrieve.
     * @return A ResponseEntity containing the Article object if found, or a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Optional<Article> article = articleRepository.findById(id);
        return article.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing article by its ID.
     * The method also updates the background color of an article based on the associated newsletter.
     *
     * @param id The ID of the article to update.
     * @param updatedArticle The updated Article object.
     * @return A ResponseEntity containing the updated Article object, or a 404 Not Found response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article updatedArticle) {
        return articleRepository.findById(id).map(article -> {
            article.setTitle(updatedArticle.getTitle());
            article.setContent(updatedArticle.getContent());
            article.setPublicationDate(updatedArticle.getPublicationDate());
            article.setLongitude(updatedArticle.getLongitude());
            article.setLatitude(updatedArticle.getLatitude());
            article.setValid(updatedArticle.isValid());
            article.setRead(updatedArticle.isRead()); // Mettre à jour l'état de lecture
            article.setImages(updatedArticle.getImages());

            // Mettre à jour la couleur de fond si la newsletter est modifiée
            if (!article.getNewsletter().equals(updatedArticle.getNewsletter())) {
                article.setNewsletter(updatedArticle.getNewsletter());
                article.setBackgroundColor(updatedArticle.getNewsletter().getBackgroundColor());
            }

            Article savedArticle = articleRepository.save(article);
            return ResponseEntity.ok(savedArticle);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes an article by its ID.
     *
     * @param id The ID of the article to delete.
     * @return A ResponseEntity with a success message if deleted, or a 404 Not Found response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id);
            return ResponseEntity.ok("Article deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Validates an article by setting its 'valid' flag to true.
     *
     * @param id The ID of the article to validate.
     * @return A ResponseEntity containing the validated Article object, or a 404 Not Found response.
     */
    @PatchMapping("/{id}/validate")
    public ResponseEntity<Article> validateArticle(@PathVariable Long id) {
        return articleRepository.findById(id).map(article -> {
            article.setValid(true); // Utilisez les bons noms de méthodes
            Article savedArticle = articleRepository.save(article);
            return ResponseEntity.ok(savedArticle);
        }).orElse(ResponseEntity.notFound().build());
    }


    /**
     * Retrieves the title of the newsletter associated with an article by the article ID.
     * @param articleId The ID of the article.
     * @return A ResponseEntity containing a map with the newsletter title, or a 404 Not Found response.
     */
    @GetMapping("/{articleId}/newsletter-title")
    public ResponseEntity<Map<String, String>> getNewsletterTitleByArticleId(@PathVariable Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("title", article.get().getNewsletter().getTitle());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }


    /**
     * Retrieves the author's full name of an article by its ID.
     *
     * @param articleId The ID of the article.
     * @return A ResponseEntity containing a map with the author's full name, or a 404 Not Found response.
     */
    @GetMapping("/{articleId}/author-name")
    public ResponseEntity<Map<String, String>> getAuthorNameByArticleId(@PathVariable Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isPresent()) {
            Map<String, String> response = new HashMap<>();
            String fullName = article.get().getAuthor().getFirstName() + " " + article.get().getAuthor().getLastName();
            response.put("name", fullName);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves all available articles (i.e., articles with valid = true).
     * @return A ResponseEntity containing a list of available articles, or a 204 No Content response if empty.
     */
    @GetMapping("/available")
    public ResponseEntity<List<Article>> getAllAvailableArticles() {
        List<Article> availableArticles = articleRepository.findByValidTrue();
        if (availableArticles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(availableArticles);
    }

    /**
     * Retrieves all unavailable articles (i.e., articles with valid = false).
     * @return A ResponseEntity containing a list of unavailable articles, or a 204 No Content response if empty.
     */
    @GetMapping("/unavailable")
    public ResponseEntity<List<Article>> getAllUnavailableArticles() {
        List<Article> unavailableArticles = articleRepository.findByValidFalse();
        if (unavailableArticles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(unavailableArticles);
    }

    /**
     * Searches for articles by a query in the title that are valid.
     *
     * @param query The query term to search for in the article titles.
     * @return A ResponseEntity containing a list of matching articles, or a 204 No Content response if empty.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(
            @RequestParam("query") String query) {
        List<Article> articles = articleRepository.searchValidArticles(query);
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    /**
     * Marks an article as read by a user.
     * It uses the Keycloak ID from the authentication object to identify the user.
     * If a read entry already exists for a user-article combination, it will be updated. If it does not exist, it will be created.
     *
     * @param articleId The ID of the article to mark as read.
     * @param authentication The authentication object containing the user's Keycloak ID.
     * @return A ResponseEntity containing a success message and a 200 Ok response, or a 404 Not Found response if the user or article does not exist.
     */
    @PatchMapping("/{articleId}/mark-read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long articleId, Authentication authentication) {
        String keycloakId = authentication.getName(); // Récupérer le Keycloak ID
        Optional<User> user = userRepository.findByKeycloakId(keycloakId);
        Optional<Article> article = articleRepository.findById(articleId);

        if (user.isEmpty() || article.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserArticleRead userArticleRead = userArticleReadRepository
                .findByUserUserIdAndArticleArticleId(user.get().getUserId(), articleId)
                .orElse(new UserArticleRead());

        userArticleRead.setUser(user.get());
        userArticleRead.setArticle(article.get());
        userArticleRead.setRead(true);

        userArticleReadRepository.save(userArticleRead);

        // Retourner une réponse JSON valide
        Map<String, String> response = new HashMap<>();
        response.put("message", "Article marked as read.");
        return ResponseEntity.ok(response);
    }


    /**
     * Marks an article as unread by a user.
     * It uses the Keycloak ID from the authentication object to identify the user.
     * If a read entry already exists for a user-article combination, it will be updated. If it does not exist, it will be created.
     * @param articleId The ID of the article to mark as unread.
     * @param authentication The authentication object containing the user's Keycloak ID.
     * @return A ResponseEntity containing a success message and a 200 Ok response, or a 404 Not Found response if the user or article does not exist.
     */
    @PatchMapping("/{articleId}/mark-unread")
    public ResponseEntity<Map<String, String>> markAsUnread(@PathVariable Long articleId, Authentication authentication) {
        String keycloakId = authentication.getName(); // Récupérer le Keycloak ID
        Optional<User> user = userRepository.findByKeycloakId(keycloakId);
        Optional<Article> article = articleRepository.findById(articleId);

        if (user.isEmpty() || article.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserArticleRead userArticleRead = userArticleReadRepository
                .findByUserUserIdAndArticleArticleId(user.get().getUserId(), articleId)
                .orElse(new UserArticleRead());

        userArticleRead.setUser(user.get());
        userArticleRead.setArticle(article.get());
        userArticleRead.setRead(false); // Marquer comme non lu

        userArticleReadRepository.save(userArticleRead);

        // Retourner une réponse JSON valide
        Map<String, String> response = new HashMap<>();
        response.put("message", "Article marked as unread.");
        return ResponseEntity.ok(response);
    }




    /**
     * Retrieves the read status of an article for a specific user.
     * Uses the Keycloak ID from the authentication object to identify the user.
     *
     * @param articleId The ID of the article.
     * @param authentication The authentication object containing the user's Keycloak ID.
     * @return A ResponseEntity containing a map with the read status (true or false) or a 404 Not Found response.
     */
    @GetMapping("/{articleId}/status")
    public ResponseEntity<Map<String, Boolean>> getArticleReadStatus(@PathVariable Long articleId, Authentication authentication) {
        String keycloakId = authentication.getName(); // Récupérer l'ID Keycloak
        Optional<User> user = userRepository.findByKeycloakId(keycloakId);
        Optional<Article> article = articleRepository.findById(articleId);

        if (user.isEmpty() || article.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<UserArticleRead> userArticleRead = userArticleReadRepository
                .findByUserUserIdAndArticleArticleId(user.get().getUserId(), articleId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isRead", userArticleRead.map(UserArticleRead::isRead).orElse(false));
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all articles that a user has marked as read.
     * It uses the Keycloak ID from the authentication object to identify the user.
     *
     * @param authentication The authentication object containing the user's Keycloak ID.
     * @return A ResponseEntity containing a list of articles that the user has read, a 204 No Content response if the user has not read any articles or a 401 Unauthorised if no user was found.
     */
    @GetMapping("/read")
    public ResponseEntity<List<Article>> getReadArticles(Authentication authentication) {
        // Récupérer le Keycloak ID de l'utilisateur connecté
        String keycloakId = authentication.getName();
        System.out.println("Keycloak ID reçu : " + keycloakId);

        // Trouver l'utilisateur correspondant
        Optional<User> user = userRepository.findByKeycloakId(keycloakId);
        if (user.isEmpty()) {
            System.out.println("Aucun utilisateur trouvé pour Keycloak ID : " + keycloakId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Rechercher les articles marqués comme lus par cet utilisateur
        List<UserArticleRead> userArticleReads = userArticleReadRepository.findByUserUserIdAndIsReadTrue(user.get().getUserId());

        // Si aucun article trouvé, renvoyer un statut HTTP 204 (No Content)
        if (userArticleReads.isEmpty()) {
            System.out.println("Aucun article lu trouvé pour l'utilisateur : " + user.get().getEmail());
            return ResponseEntity.noContent().build();
        }

        // Extraire les articles depuis la liste UserArticleRead
        List<Article> readArticles = userArticleReads.stream()
                .map(UserArticleRead::getArticle)
                .toList();

        return ResponseEntity.ok(readArticles);
    }


    /**
     * Retrieves all articles written by an author using their email address.
     * @param email The email address of the author.
     * @return A ResponseEntity containing a list of articles written by this author.
     * @throws RuntimeException if the user is not found with the given email.
     */
    @GetMapping("/author/email/{email}")
    public ResponseEntity<List<Article>> getArticlesByAuthorEmail(@PathVariable String email) {
        // Trouver l'utilisateur via son email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));

        // Récupérer les articles associés à cet utilisateur
        List<Article> articles = articleRepository.findByAuthorId(user.getUserId());

        return ResponseEntity.ok(articles);
    }









}
