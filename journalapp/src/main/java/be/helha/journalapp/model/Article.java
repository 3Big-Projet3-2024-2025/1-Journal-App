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

    private String title; // Champ renommé pour respecter les conventions
    private String content;
    private String publicationDate; // Respectez le camelCase
    private double longitude;
    private double latitude;

    private boolean valid; // Champ boolean corrigé (sans underscore)

    // Relation Many-to-One avec Newsletter
    @ManyToOne
    @JoinColumn(name = "newsletter_id") // Nom de la clé étrangère
    private Newsletter newsletter;

    // Relation One-to-Many avec Image
    @OneToMany(cascade = CascadeType.ALL)
    private List<Image> images;
}
