package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Entity
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long articleId;

    private String title;
    private String content;
    private String publicationDate;
    private double longitude;
    private double latitude;

    private boolean valid;

    // Many-to-One relationship with Newsletter
    @ManyToOne
    @JoinColumn(name = "newsletter_id", nullable = false)// Foreign key to Newsletter
    private Newsletter newsletter;



    // Many-to-One relationship with User (an article is written by a single user)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User
    private User author;

    // One-to-Many relationship with Image (an article can have multiple images)
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Image> images;
}

