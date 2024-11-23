package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Lob
    private byte[] imagePath; // Used to store binary data (such as an image)

    // Many-to-One relationship with Article (each image belongs to a single article)
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false) // Foreign key to Article
    private Article article;
}
