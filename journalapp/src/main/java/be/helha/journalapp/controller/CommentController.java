package be.helha.journalapp.controller;

import be.helha.journalapp.model.Comment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private List<Comment> comments = new ArrayList<>(); // In-memory storage for comments
    private Long currentId = 1L; // Counter for generating unique IDs

    // CREATE: Add a new comment
    @PostMapping
    public Comment addComment(@RequestBody Comment newComment) {
        newComment.setComment_Id(currentId++); // Set a unique ID for the new comment
        comments.add(newComment); // Add the comment to the list
        return newComment; // Return the created comment
    }

    // READ: Retrieve all comments
    @GetMapping
    public List<Comment> getAllComments() {
        return comments; // Return the list of comments
    }

    // READ: Retrieve a specific comment by its ID
    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable Long id) {
        return comments.stream()
                .filter(comment -> comment.getComment_Id().equals(id)) // Find the comment with the matching ID
                .findFirst()
                .orElse(null); // Return null if no comment is found
    }

    // UPDATE: Update an existing comment
    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable Long id, @RequestBody Comment updatedComment) {
        for (Comment comment : comments) {
            if (comment.getComment_Id().equals(id)) { // Check if the ID matches
                comment.setContent(updatedComment.getContent()); // Update the content
                comment.setPublication_Date(updatedComment.getPublication_Date()); // Update the publication date
                return comment; // Return the updated comment
            }
        }
        return null; // Return null if no comment is found
    }

    // DELETE: Delete a comment by its ID
    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable Long id) {
        boolean removed = comments.removeIf(comment -> comment.getComment_Id().equals(id)); // Remove the comment
        return removed ? "Comment deleted successfully" : "Comment not found"; // Return status message
    }
}
