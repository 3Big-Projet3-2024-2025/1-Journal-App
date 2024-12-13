package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Newsletter;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.NewsletterRepository;
import be.helha.journalapp.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/articles")
public class ArticleController {


    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NewsletterRepository newsletterRepository;

    public ArticleController(ArticleRepository articleRepository, UserRepository userRepository, NewsletterRepository newsletterRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.newsletterRepository = newsletterRepository;
    }


    @PostMapping
    public ResponseEntity<Article> addArticle(@RequestBody Map<String, Object> articleData) {
        Article article = new Article();
        article.setTitle((String) articleData.get("title"));
        article.setContent((String) articleData.get("content"));
        article.setPublicationDate((String) articleData.get("publicationDate"));
        article.setLongitude((Double) articleData.get("longitude"));
        article.setLatitude((Double) articleData.get("latitude"));
        article.setValid((Boolean) articleData.get("valid"));

        // Convertir les IDs en entités
        Long newsletterId = ((Number) articleData.get("newsletter_id")).longValue();
        Long userId = ((Number) articleData.get("user_id")).longValue();

        Newsletter newsletter = newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new RuntimeException("Newsletter not found"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        article.setNewsletter(newsletter);
        article.setAuthor(author);

        Article savedArticle = articleRepository.save(article);
        return ResponseEntity.ok(savedArticle);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Optional<Article> article = articleRepository.findById(id);
        return article.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article updatedArticle) {
        return articleRepository.findById(id).map(article -> {
            article.setTitle(updatedArticle.getTitle());
            article.setContent(updatedArticle.getContent());
            article.setPublicationDate(updatedArticle.getPublicationDate());
            article.setLongitude(updatedArticle.getLongitude());
            article.setLatitude(updatedArticle.getLatitude());
            article.setValid(updatedArticle.isValid()); // Utilisez les bons noms de méthodes
            article.setImages(updatedArticle.getImages());

            Article savedArticle = articleRepository.save(article);
            return ResponseEntity.ok(savedArticle);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id);
            return ResponseEntity.ok("Article deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/validate")
    public ResponseEntity<Article> validateArticle(@PathVariable Long id) {
        return articleRepository.findById(id).map(article -> {
            article.setValid(true); // Utilisez les bons noms de méthodes
            Article savedArticle = articleRepository.save(article);
            return ResponseEntity.ok(savedArticle);
        }).orElse(ResponseEntity.notFound().build());
    }


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

    @GetMapping("/available")
    public ResponseEntity<List<Article>> getAllAvailableArticles() {
        List<Article> availableArticles = articleRepository.findByValidTrue();
        if (availableArticles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(availableArticles);
    }

    @GetMapping("/unavailable")
    public ResponseEntity<List<Article>> getAllUnavailableArticles() {
        List<Article> unavailableArticles = articleRepository.findByValidFalse();
        if (unavailableArticles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(unavailableArticles);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(
            @RequestParam("query") String query) {
        List<Article> articles = articleRepository.searchValidArticles(query);
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }
}