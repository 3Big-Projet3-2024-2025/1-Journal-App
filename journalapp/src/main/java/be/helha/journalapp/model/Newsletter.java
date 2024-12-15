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

    private String backgroundColor;
    private String titleFont;
    private int titleFontSize;
    private String titleColor;
    private boolean titleBold;
    private boolean titleUnderline;

    private String subtitleFont;
    private int subtitleFontSize;
    private String subtitleColor;
    private boolean subtitleBold;
    private boolean subtitleItalic;

    private String textAlign;


    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @JsonIgnore
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Article> articles;

    @JsonIgnore
    @OneToMany(mappedBy = "newsletter", cascade = CascadeType.ALL)
    private List<Comment> comments;
}

