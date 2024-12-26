package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents the read status of an article by a user.
 * This entity tracks which users have read which articles.
 */
@Entity
@Data
public class UserArticleRead {

    /**
     * The unique identifier for the UserArticleRead entry.
     * It's auto-generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-One relationship with {@link User}.
     * Represents the user who has read the article.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Many-to-One relationship with {@link Article}.
     * Represents the article that has been read.
     */
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    /**
     * Indicates whether the user has read the article (true) or not (false).
     */
    private boolean isRead;
}