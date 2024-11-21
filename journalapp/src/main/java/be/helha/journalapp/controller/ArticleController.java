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

    // Constructor to inject the repository
    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // CREATE: Add a new article
    @PostMapping
    public ResponseEntity<Article> addArticle(@RequestBody Article newArticle) {
        Article savedArticle = articleRepository.save(newArticle); // Save the article to the database
        return ResponseEntity.ok(savedArticle); // Return the created article
    }

    // READ: Retrieve all articles
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleRepository.findAll(); // Retrieve all articles from the database
        return ResponseEntity.ok(articles); // Return the list of articles
    }

    // READ: Retrieve a specific article by ID
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Optional<Article> article = articleRepository.findById(id); // Retrieve the article by ID
        return article.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Return the article or a 404 response
    }

    // UPDATE: Update an existing article's details
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article updatedArticle) {
        Optional<Article> existingArticle = articleRepository.findById(id);

        if (existingArticle.isPresent()) {
            Article article = existingArticle.get();
            article.setTitle(updatedArticle.getTitle()); // Update the title
            article.setContent(updatedArticle.getContent()); // Update the content
            article.setPublication_Date(updatedArticle.getPublication_Date()); // Update the publication date
            article.setLongitude(updatedArticle.getLongitude()); // Update the longitude
            article.setLatitude(updatedArticle.getLatitude()); // Update the latitude
            article.setIs_Valid(updatedArticle.isIs_Valid()); // Update the validation status
            article.setImages(updatedArticle.getImages()); // Update the list of images

            Article savedArticle = articleRepository.save(article); // Save the updated article
            return ResponseEntity.ok(savedArticle); // Return the updated article
        }

        return ResponseEntity.notFound().build(); // Return a 404 response if the article is not found
    }

    // DELETE: Delete an article by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id); // Delete the article
            return ResponseEntity.ok("Article deleted successfully");
        }
        return ResponseEntity.notFound().build(); // Return a 404 response if the article is not found
    }

    // Additional: Validate an article
    @PatchMapping("/{id}/validate")
    public ResponseEntity<Article> validateArticle(@PathVariable Long id) {
        Optional<Article> existingArticle = articleRepository.findById(id);

        if (existingArticle.isPresent()) {
            Article article = existingArticle.get();
            article.setIs_Valid(true); // Mark the article as validated
            Article savedArticle = articleRepository.save(article); // Save the updated article
            return ResponseEntity.ok(savedArticle); // Return the updated article
        }

        return ResponseEntity.notFound().build(); // Return a 404 response if the article is not found
    }
}
