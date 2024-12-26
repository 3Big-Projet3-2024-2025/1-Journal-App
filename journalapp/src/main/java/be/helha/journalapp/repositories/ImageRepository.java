package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface defines the data access methods for the {@link Image} entity.
 * It extends JpaRepository to provide standard database operations and adds custom queries for specific requirements.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Finds all images associated with a specific article ID.
     * @param articleId The ID of the article.
     * @return A list of images belonging to the specified article.
     */
    List<Image> findByArticleArticleId(Long articleId);
}