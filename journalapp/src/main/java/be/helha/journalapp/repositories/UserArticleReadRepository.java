package be.helha.journalapp.repositories;

import be.helha.journalapp.model.UserArticleRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface defines the data access methods for the {@link UserArticleRead} entity.
 * It extends JpaRepository to provide standard database operations and adds custom queries for specific requirements.
 */
public interface UserArticleReadRepository extends JpaRepository<UserArticleRead, Long> {

    /**
     * Finds a UserArticleRead entry by user ID and article ID.
     *
     * @param userId The ID of the user.
     * @param articleId The ID of the article.
     * @return An Optional containing the UserArticleRead entry if found, or empty if not found.
     */
    Optional<UserArticleRead> findByUserUserIdAndArticleArticleId(Long userId, Long articleId);

    /**
     * Finds all UserArticleRead entries for a specific user that are marked as read.
     *
     * @param userId The ID of the user.
     * @return A list of UserArticleRead entries for the specified user that are marked as read.
     */
    List<UserArticleRead> findByUserUserIdAndIsReadTrue(Long userId);
}