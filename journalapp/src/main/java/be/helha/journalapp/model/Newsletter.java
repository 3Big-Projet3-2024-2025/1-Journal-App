package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Entity
@Table(name = "Newsletter")
@Data
public class Newsletter {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long Newsletter_Id;
    private String Title;
    private String Subtitle;
    private String PublicationDate;
    private boolean IsRead;

    // Relation One-to-Many with Article
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Article> articles;

    // Relation Many-to-One with User
    @ManyToOne
    @JoinColumn(name = "utilisateur_id") // Clé étrangère dans la table Newsletter
    private User users;

    // Relation One-to-Many avec Commentaire
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

}
