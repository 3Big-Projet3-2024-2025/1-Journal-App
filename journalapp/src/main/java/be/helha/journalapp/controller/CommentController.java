package be.helha.journalapp.controller;

import be.helha.journalapp.model.Comment;
import be.helha.journalapp.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // CREATE: Add a new comment
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment newComment) {
        Comment savedComment = commentRepository.save(newComment);
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
                    existingComment.setPublication_Date(updatedComment.getPublication_Date());
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

    // Additional: Retrieve comments by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> getCommentsByUserId(@PathVariable Long userId) {
        List<Comment> comments = commentRepository.findByUsers_UserId(userId);
        return ResponseEntity.ok(comments);
    }

    // Additional: Retrieve comments by newsletter ID
    @GetMapping("/newsletter/{newsletterId}")
    public ResponseEntity<List<Comment>> getCommentsByNewsletterId(@PathVariable Long newsletterId) {
        List<Comment> comments = commentRepository.findByNewsletter_Newsletter_Id(newsletterId);
        return ResponseEntity.ok(comments);
    }
}
