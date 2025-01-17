package be.helha.journalapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an article entity in the application.
 * This entity is mapped to a database table and includes various fields and relationships to other entities.
 */
@Entity
@Data
public class Article {
    /**
     * The unique identifier for the article.
     * It's auto-generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    /**
     * The title of the article.
     */
    private String title;

    /**
     * The content of the article.
     * The length is set to 5000 characters to accommodate long articles.
     */
    @Column(length = 5000)
    private String content;

    /**
     * The publication date of the article.
     */
    private String publicationDate;

    /**
     * The longitude coordinate related to the article.
     */
    private double longitude;

    /**
     * The latitude coordinate related to the article.
     */
    private double latitude;

    /**
     * Indicates if the article is valid or not.
     */
    private boolean valid;

    /**
     * Indicates if the article is read by user or not
     */
    private boolean read;
    /**
     * The background color of the article.
     */
    private String backgroundColor;

    /**
     * Many-to-One relationship with {@link Newsletter}.
     * An article belongs to a single newsletter.
     */
    @ManyToOne
    @JoinColumn(name = "newsletter_id")
    private Newsletter newsletter;

    /**
     * Many-to-One relationship with {@link User}.
     * An article is written by a single user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;


    /**
     * One-to-Many relationship with {@link Image}.
     * An article can have multiple images.
     * The `JsonIgnore` annotation is used to prevent infinite recursion when serializing to JSON.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL /*, orphanRemoval = true*/)
    private List<Image> images = new ArrayList<>();

    /**
     * Utility method to add an image to the article and set the article on the image.
     *
     * @param image The image to add.
     */
    public void addImage(Image image) {
        images.add(image);
        image.setArticle(this);
    }

    /**
     * Utility method to remove an image from the article and nullify the article from the image.
     *
     * @param image The image to remove.
     */
    public void removeImage(Image image) {
        images.remove(image);
        image.setArticle(null);
    }

    /**
     * One-to-Many relationship with {@link UserArticleRead}.
     * Represents the users that have read the article.
     *  The `JsonIgnore` annotation is used to prevent infinite recursion when serializing to JSON.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<UserArticleRead> userReads;

}