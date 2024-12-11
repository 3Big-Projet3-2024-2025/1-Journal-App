package be.helha.journalapp.model;

import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private boolean isRead;

    // One-to-Many relationship with Article (not serialized to JSON or Swagger)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Article> articles;

    // Many-to-One relationship with User (creator of the newsletter)
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // One-to-Many relationship with Comment (not serialized to JSON or Swagger)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Comment> comments;
}

