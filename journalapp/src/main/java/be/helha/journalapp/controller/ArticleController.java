package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.repositories.ArticleRepository;
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

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @PostMapping
    public ResponseEntity<Article> addArticle(@RequestBody Article newArticle) {
        Article savedArticle = articleRepository.save(newArticle);
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
}
