package be.helha.journalapp.repositories;

import be.helha.journalapp.model.UserArticleRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserArticleReadRepository extends JpaRepository<UserArticleRead, Long> {
    Optional<UserArticleRead> findByUserUserIdAndArticleArticleId(Long userId, Long articleId);
    List<UserArticleRead> findByUserUserIdAndIsReadTrue(Long userId);
}
