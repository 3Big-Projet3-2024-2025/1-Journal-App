package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Commentaire")
@Data
public class Comment {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long Comment_Id;            // Unique identifier for the comment
    private String Content;            // Content of the comment
    private String Publication_Date;    // Date the comment was published

    // Relation Many-to-One with User
    @ManyToOne
    @JoinColumn()
    private User users;

    // Relation Many-to-One avec Newsletter
    @ManyToOne
    @JoinColumn() // Clé étrangère dans la table Commentaire
    private Newsletter newsletter;

}
