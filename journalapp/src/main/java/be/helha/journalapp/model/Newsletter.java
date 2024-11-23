package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
@Entity
@Data
public class Newsletter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsletterId;

    private String title;
    private String subtitle;
    private String publicationDate;
    private boolean isRead;

    // One-to-Many relationship with Article (a newsletter contains multiple articles)
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Article> articles;

    // Many-to-One relationship with User (the creator of the newsletter)
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false) // Clé étrangère vers User
    private User creator;

    // One-to-Many relationship with Comment (a newsletter can receive multiple comments)
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Comment> comments;
}

