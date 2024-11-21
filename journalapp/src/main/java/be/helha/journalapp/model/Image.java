package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId; // Renommé pour respecter les conventions camelCase
    private byte[] imagePath; // Renommé en camelCase

    // Relation Many-to-One avec Article
    @ManyToOne
    @JoinColumn(name = "article_id") // Clé étrangère pour associer une image à un article
    private Article article;
}
