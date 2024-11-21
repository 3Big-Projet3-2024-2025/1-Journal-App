package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long>/* long for id type*/ {

    /**
     * Finds newsletters by title.
     *
     * @param title The title of the newsletter.
     * @return A list of newsletters with the given title.
     */
    List<Newsletter> findByTitle(String title);
}
