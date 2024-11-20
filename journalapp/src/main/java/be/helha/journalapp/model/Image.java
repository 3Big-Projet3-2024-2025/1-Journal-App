package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Image")
@Data
public class Image {
    @jakarta.persistence.Id
    @Id
    private Long Image_Id;
    private byte [] Image_Path;

    // Relation One-to-One avec Newsletter (bidirectionnelle si nécessaire)
    @OneToOne(mappedBy = "image")
    private Newsletter newsletter;

    // Relation Many-to-One avec Image
    @ManyToOne
    @JoinColumn(name = "newsletter_id") // Clé étrangère dans la table Image
    private Article article;
}
