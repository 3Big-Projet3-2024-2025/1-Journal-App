package be.helha.journalapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Commentaire")
@Data
public class Comment {
    @Id
    private Long commentId;            // Unique identifier for the comment
    private String content;            // Content of the comment
    private String publicationDate;    // Date the comment was published
}
