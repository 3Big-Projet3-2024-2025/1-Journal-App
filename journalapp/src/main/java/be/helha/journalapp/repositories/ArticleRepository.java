package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByNewsletterNewsletterId(Long newsletterId);
    @Query("SELECT a FROM Article a WHERE a.newsletter.newsletterId = :newsletterId")
    List<Article> findByNewsletterId(@Param("newsletterId") Long newsletterId);
    @Query("SELECT a FROM Article a WHERE a.author.userId = :userId")
    List<Article> findByAuthorId(@Param("userId") Long userId);
    List<Article> findByValidTrue();
    List<Article> findByValidFalse();
}
