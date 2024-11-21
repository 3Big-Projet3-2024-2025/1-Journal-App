package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Find articles by title (partial match or exact)
    List<Article> findByTitleContainingIgnoreCase(String title);

    // Find articles by validation status
    List<Article> findByIsValid(boolean isValid);

    // Find articles by publication date
    List<Article> findByPublicationDate(String publicationDate);

    // Find articles by newsletter ID
    List<Article> findByNewsletter_Newsletter_Id(Long newsletterId);
}
