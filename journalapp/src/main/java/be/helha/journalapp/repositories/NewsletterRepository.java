package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour gérer les opérations de persistance des entités {@link Newsletter}.
 * Cette interface étend {@link JpaRepository} pour fournir des opérations CRUD de base
 * ainsi que des méthodes de requête personnalisées pour l'entité Newsletter.
 *
 * @see JpaRepository
 */
@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {

    /**
     * Trouve toutes les newsletters ayant un titre spécifique.
     *
     * @param title le titre de la newsletter recherché
     * @return une liste de newsletters correspondant au titre donné
     */
    List<Newsletter> findByTitle(String title);

    /**
     * Récupère la liste des newsletters auxquelles un journaliste spécifique (utilisateur) est associé.
     *
     * @param userId l'ID du journaliste (utilisateur)
     * @return une liste de newsletters associées au journaliste donné
     */
    @Query("SELECT n FROM Newsletter n JOIN n.journalists j WHERE j.userId = :userId")
    List<Newsletter> findByJournalistUserId(@Param("userId") Long userId);
}
