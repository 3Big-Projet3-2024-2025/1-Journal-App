package be.helha.journalapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String content;
    private String publicationDate;

    // Many-to-One relationship with User (each comment belongs to a user)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)// Foreign key to User
    private User user;

    // Many-to-One relationship with Newsletter (each comment belongs to a newsletter)
    //@JsonIgnore
    //@ApiModelProperty(hidden = true)
    @ManyToOne
    @JoinColumn(name = "newsletterId",nullable = true) // Foreign key to Newsletter
    private Newsletter newsletter;
}

