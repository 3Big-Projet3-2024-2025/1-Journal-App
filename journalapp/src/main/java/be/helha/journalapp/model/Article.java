package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "Article")
@Data
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long Article_Id;
    private String Title;
    private String Content;
    private String Publication_Date;
    private double Longitude;
    private double Latitude;
    private boolean Is_Valid;
    // Relation Many-to-One avec Newsletter
    @ManyToOne
    @JoinColumn() // Clé étrangère dans la table Article
    private Newsletter newsletter;

    // Relation One-to-Many avec Image
    @OneToMany( cascade = CascadeType.ALL)
    private List<Image> images;
}
