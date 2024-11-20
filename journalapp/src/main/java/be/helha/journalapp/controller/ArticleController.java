package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private List<Article> articles = new ArrayList<>(); // In-memory storage for articles
    private Long currentId = 1L; // Counter for generating unique IDs

    // CREATE: Add a new article
    @PostMapping
    public Article addArticle(@RequestBody Article newArticle) {
        newArticle.setArticle_Id(currentId++); // Generate a unique ID
        articles.add(newArticle); // Add the article to the list
        return newArticle; // Return the created article
    }

    // READ: Retrieve all articles
    @GetMapping
    public List<Article> getAllArticles() {
        return articles; // Return the list of articles
    }

    // READ: Retrieve a specific article by ID
    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable Long id) {
        return articles.stream()
                .filter(article -> article.getArticle_Id().equals(id)) // Find the article with the matching ID
                .findFirst()
                .orElse(null); // Return null if no article is found
    }

    // UPDATE: Update an existing article's details
    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody Article updatedArticle) {
        for (Article article : articles) {
            if (article.getArticle_Id().equals(id)) { // Check if the ID matches
                article.setTitle(updatedArticle.getTitle()); // Update the title
                article.setContent(updatedArticle.getContent()); // Update the content
                article.setPublication_Date(updatedArticle.getPublication_Date()); // Update the publication date
                article.setLongitude(updatedArticle.getLongitude()); // Update the longitude
                article.setLatitude(updatedArticle.getLatitude()); // Update the latitude
                article.setIs_Valid(updatedArticle.isIs_Valid()); // Update the validation status
                article.setImages(updatedArticle.getImages()); // Update the list of images
                return article; // Return the updated article
            }
        }
        return null; // Return null if no article is found
    }

    // DELETE: Delete an article by ID
    @DeleteMapping("/{id}")
    public String deleteArticle(@PathVariable Long id) {
        boolean removed = articles.removeIf(article -> article.getArticle_Id().equals(id)); // Remove the article
        return removed ? "Article deleted successfully" : "Article not found"; // Return a status message
    }

    // Additional: Validate an article
    @PatchMapping("/{id}/validate")
    public Article validateArticle(@PathVariable Long id) {
        for (Article article : articles) {
            if (article.getArticle_Id().equals(id)) { // Check if the ID matches
                article.setIs_Valid(true); // Mark the article as validated
                return article; // Return the updated article
            }
        }
        return null; // Return null if no article is found
    }
}
