package be.helha.journalapp.model;

import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Représente une entité de newsletter dans l'application.
 * Cette entité est mappée à une table de base de données et inclut divers champs et relations avec d'autres entités.
 */
@Entity
@Data
public class Newsletter {

    /**
     * L'identifiant unique de la newsletter.
     * Il est auto-généré par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsletterId;

    /**
     * Le titre de la newsletter.
     */
    private String title;

    /**
     * Le sous-titre de la newsletter.
     */
    private String subtitle;

    /**
     * La date de publication de la newsletter.
     */
    private String publicationDate;

    /**
     * La couleur de fond de la newsletter.
     */
    private String backgroundColor;

    /**
     * La police utilisée pour le titre de la newsletter.
     */
    private String titleFont;

    /**
     * La taille de la police utilisée pour le titre de la newsletter.
     */
    private int titleFontSize;

    /**
     * La couleur du texte du titre dans la newsletter.
     */
    private String titleColor;

    /**
     * Indique si le texte du titre est en gras.
     */
    private boolean titleBold;

    /**
     * Indique si le texte du titre est souligné.
     */
    private boolean titleUnderline;

    /**
     * La police utilisée pour le sous-titre de la newsletter.
     */
    private String subtitleFont;

    /**
     * La taille de la police utilisée pour le sous-titre de la newsletter.
     */
    private int subtitleFontSize;

    /**
     * La couleur du texte du sous-titre dans la newsletter.
     */
    private String subtitleColor;

    /**
     * Indique si le texte du sous-titre est en gras.
     */
    private boolean subtitleBold;

    /**
     * Indique si le texte du sous-titre est en italique.
     */
    private boolean subtitleItalic;

    /**
     * L'alignement du texte utilisé dans la newsletter (par exemple, gauche, centre, droite).
     */
    private String textAlign;

    /**
     * Relation Many-to-One avec {@link User}.
     * Une newsletter est créée par un seul utilisateur.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /**
     * Relation One-to-Many avec {@link Article}.
     * Une newsletter peut contenir plusieurs articles.
     * L'annotation {@code @JsonIgnore} est utilisée pour éviter les récursions infinies lors de la sérialisation en JSON.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Article> articles;

    /**
     * Relation Many-to-Many avec {@link User} pour les journalistes.
     * Une newsletter peut avoir plusieurs journalistes, et un journaliste peut être associé à plusieurs newsletters.
     * L'annotation {@code @JoinTable} définit la table de jointure et les clés étrangères.
     */
    @ManyToMany
    @JoinTable(
            name = "newsletter_journalists",
            joinColumns = @JoinColumn(name = "newsletter_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    private List<User> journalists;
}
