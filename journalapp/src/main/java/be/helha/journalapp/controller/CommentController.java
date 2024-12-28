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

/**
 * REST Controller for managing comments in the Journal application.
 * Provides endpoints to create, retrieve, update, and delete comments, as well as
 * retrieve comments by user or article.
 *
 * Endpoints:
 * - POST /comments: Add a new comment
 * - GET /comments: Retrieve all comments
 * - GET /comments/{id}: Retrieve a comment by its ID
 * - PUT /comments/{id}: Update a specific comment
 * - DELETE /comments/{id}: Delete a specific comment
 * - GET /comments/user/{userId}: Retrieve comments by a specific user
 * - GET /comments/article/{articleId}: Retrieve comments for a specific article
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    /**
     * Repository for managing comment data.
     */
    private final CommentRepository commentRepository;

    /**
     * Repository for managing user data.
     */
    private final UserRepository userRepository;

    /**
     * Repository for managing article data.
     */
    private final ArticleRepository articleRepository;

    /**
     * Constructs a new instance of CommentController with required dependencies.
     *
     * @param commentRepository the repository for comment data
     * @param userRepository the repository for user data
     * @param articleRepository the repository for article data
     */
    @Autowired
    public CommentController(CommentRepository commentRepository, UserRepository userRepository, ArticleRepository articleRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    /**
     * Adds a new comment to the database.
     *
     * @param commentData the data for the new comment, including user ID, article ID, content, and publication date
     * @return a ResponseEntity containing the created comment and HTTP status code 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Map<String, Object> commentData) {
        System.out.println("Received data: " + commentData);

        if (!commentData.containsKey("user_id")) {
            throw new RuntimeException("User ID is missing from request.");
        }
        if (!commentData.containsKey("article_id")) {
            throw new RuntimeException("Article ID is missing from request.");
        }

        String content = (String) commentData.get("content");
        String publicationDate = (String) commentData.get("publicationDate");

        Long userId = ((Number) commentData.get("user_id")).longValue();
        Long articleId = ((Number) commentData.get("article_id")).longValue();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found."));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article with ID " + articleId + " not found."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPublicationDate(publicationDate);
        comment.setUser(user);
        comment.setArticle(article);

        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    /**
     * Retrieves all comments from the database.
     *
     * @return a ResponseEntity containing the list of all comments and HTTP status code 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return ResponseEntity.ok(comments);
    }

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param id the ID of the comment to retrieve
     * @return a ResponseEntity containing the requested comment or HTTP status code 404 (Not Found) if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        return commentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Updates an existing comment.
     *
     * @param id the ID of the comment to update
     * @param updatedComment the updated comment data
     * @return a ResponseEntity containing the updated comment or HTTP status code 404 (Not Found) if not found
     */
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

    /**
     * Deletes a specific comment by its ID.
     *
     * @param id the ID of the comment to delete
     * @return a ResponseEntity containing a success message or HTTP status code 404 (Not Found) if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return ResponseEntity.ok("Comment deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        }
    }

    /**
     * Retrieves comments associated with a specific user.
     *
     * @param userId the ID of the user whose comments are to be retrieved
     * @return a ResponseEntity containing the list of comments for the specified user and HTTP status code 200 (OK)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable Long userId) {
        List<Comment> comments = commentRepository.findByUserUserId(userId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Retrieves comments associated with a specific article.
     *
     * @param articleId the ID of the article whose comments are to be retrieved
     * @return a ResponseEntity containing the list of comments for the specified article and HTTP status code 200 (OK)
     */
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<Comment>> getCommentsByArticleId(@PathVariable Long articleId) {
        List<Comment> comments = commentRepository.findByArticleArticleId(articleId);
        return ResponseEntity.ok(comments);
    }
}
