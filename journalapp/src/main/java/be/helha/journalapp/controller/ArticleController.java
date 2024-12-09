package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.repositories.ArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
