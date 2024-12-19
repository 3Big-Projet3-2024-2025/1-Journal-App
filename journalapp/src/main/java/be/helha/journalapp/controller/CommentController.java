package be.helha.journalapp.controller;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Comment;
import be.helha.journalapp.model.User;
import be.helha.journalapp.repositories.ArticleRepository;
import be.helha.journalapp.repositories.CommentRepository;
import be.helha.journalapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository, UserRepository userRepository, ArticleRepository articleRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Map<String, Object> commentData) {
        System.out.println("Données reçues : " + commentData);

        // Vérification de la présence des champs requis
        if (!commentData.containsKey("user_id")) {
            throw new RuntimeException("User ID is missing from request.");
        }
        if (!commentData.containsKey("article_id")) {
            throw new RuntimeException("Article ID is missing from request.");
        }

        // Récupération des informations de base
        String content = (String) commentData.get("content");
        String publicationDate = (String) commentData.get("publicationDate");

        Long userId = ((Number) commentData.get("user_id")).longValue();
        Long articleId = ((Number) commentData.get("article_id")).longValue();

        // Recherche de l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));

        // Recherche de l'article
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article with ID " + articleId + " not found."));

        // Création du commentaire
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPublicationDate(publicationDate);
        comment.setUser(user);
        comment.setArticle(article);

        // Sauvegarde du commentaire
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    // READ: Retrieve all comments
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return ResponseEntity.ok(comments);
    }

    // READ: Retrieve a specific comment by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return commentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // UPDATE: Update an existing comment
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        return commentRepository.findById(id)
                .map(existingComment -> {
                    existingComment.setContent(updatedComment.getContent());
                    existingComment.setPublicationDate(updatedComment.getPublicationDate());
                    Comment savedComment = commentRepository.save(existingComment);
                    return ResponseEntity.ok(savedComment);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // DELETE: Delete a comment by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return ResponseEntity.ok("Comment deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        }
    }

    // Retrieve comments by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable Long userId) {
        List<Comment> comments = commentRepository.findByUserUserId(userId);
        return ResponseEntity.ok(comments);
    }

    // Retrieve comments by article ID
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<Comment>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<Comment> comments = commentRepository.findByArticleArticleId(articleId);
        return ResponseEntity.ok(comments);
    }

}
