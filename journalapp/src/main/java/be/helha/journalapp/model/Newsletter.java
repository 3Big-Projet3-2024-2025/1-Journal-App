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
    private boolean read; // Notez l'utilisation de "read" au lieu de "isRead"

    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Article> articles;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
