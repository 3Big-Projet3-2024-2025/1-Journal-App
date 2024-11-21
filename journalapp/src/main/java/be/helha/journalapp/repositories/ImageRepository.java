package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // search all images associées à newsletter par ID de newsletter
    List<Image> findByNewsletter_Newsletter_Id(Long newsletterId);

    // Rechercher toutes les images associées à un article par ID de l'article
    List<Image> findByArticle_ArticleId(Long articleId);
}
