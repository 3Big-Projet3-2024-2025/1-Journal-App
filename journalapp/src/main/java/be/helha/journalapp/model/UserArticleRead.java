package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserArticleRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    private boolean isRead;
}
