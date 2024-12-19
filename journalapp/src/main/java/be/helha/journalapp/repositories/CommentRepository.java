package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserUserId(Long userId);
    List<Comment> findByArticleArticleId(Long articleId);
    List<Comment> findByContentContainingIgnoreCase(String keyword);

}
