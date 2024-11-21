package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find comments by user ID
    List<Comment> findByUsers_UserId(Long userId);

    // Find comments by newsletter ID
    List<Comment> findByNewsletter_Newsletter_Id(Long newsletterId);

    // Find comments by content containing a specific keyword (case-insensitive)
    List<Comment> findByContentContainingIgnoreCase(String keyword);
}
