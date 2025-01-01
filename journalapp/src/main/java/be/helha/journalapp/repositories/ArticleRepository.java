package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Article;
import be.helha.journalapp.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface defines the data access methods for the {@link Article} entity.
 * It extends JpaRepository to provide standard database operations and adds custom queries for specific requirements.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Finds all articles associated with a specific newsletter ID using the method name convention.
     * @param newsletterId The ID of the newsletter.
     * @return A list of articles belonging to the specified newsletter.
     */
    List<Article> findByNewsletterNewsletterId(Long newsletterId);

    /**
     * Finds all articles associated with a specific newsletter ID using a JPQL query.
     * This method is an alternative way to fetch articles by newsletter ID.
     * @param newsletterId The ID of the newsletter.
     * @return A list of articles belonging to the specified newsletter.
     */
    @Query("SELECT a FROM Article a WHERE a.newsletter.newsletterId = :newsletterId")
    List<Article> findByNewsletterId(@Param("newsletterId") Long newsletterId);

    /**
     * Finds all articles written by a specific user ID using a JPQL query.
     * @param userId The ID of the user.
     * @return A list of articles written by the specified user.
     */
    @Query("SELECT a FROM Article a WHERE a.author.userId = :userId")
    List<Article> findByAuthorId(@Param("userId") Long userId);


    /**
     * Retrieves all articles that have been marked as valid.
     * @return A list of valid articles.
     */
    List<Article> findByValidTrue();

    /**
     * Retrieves all articles that have been marked as not valid.
     * @return A list of invalid articles.
     */
    List<Article> findByValidFalse();


    /**
     * Finds all articles associated with a specific newsletter ID, using a different method name to fetch articles
     * @param newsletterId The ID of the newsletter.
     * @return A list of articles belonging to the specified newsletter.
     */
    @Query("SELECT a FROM Article a WHERE a.newsletter.newsletterId = :newsletterId")
    List<Article> findArticlesByNewsletterId(@Param("newsletterId") Long newsletterId);

    @Query("SELECT a FROM Article a WHERE a.newsletter.creator.userId = :editorId")
    List<Article> findArticlesByEditorId(@Param("editorId") Long editorId);




    /**
     * Searches for valid articles that match a given term in their titles using a JPQL query.
     * The search is case-insensitive.
     * @param term The search term.
     * @return A list of valid articles containing the search term in their title.
     */
    @Query("SELECT a FROM Article a WHERE a.valid = true AND " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Article> searchValidArticles(@Param("term") String term);
}