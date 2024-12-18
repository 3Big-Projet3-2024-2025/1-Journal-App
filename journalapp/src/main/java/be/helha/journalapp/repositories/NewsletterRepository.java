package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    List<Newsletter> findByTitle(String title);
    // Récupère la liste des newsletters auxquelles appartient un journaliste
    @Query("SELECT n FROM Newsletter n JOIN n.journalists j WHERE j.userId = :userId")
    List<Newsletter> findByJournalistUserId(@Param("userId") Long userId);
}
