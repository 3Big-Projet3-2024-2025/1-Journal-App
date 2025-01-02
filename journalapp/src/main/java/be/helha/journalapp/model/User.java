package be.helha.journalapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String lastName;
    private String firstName;
    private String email;
    private boolean isAuthorized;
    private boolean isRoleChange;
    private String keycloakId;

    // user has a single role
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // Clé étrangère vers Role
    private Role role;
    // A user can write multiple articles
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Article> articles;

    // A user can create multiple newsletters
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Newsletter> newsletters;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserArticleRead> articleReads;

    // New field: List to store GDPR requests (optional)
    @ElementCollection
    @CollectionTable(name = "user_gdpr_requests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "gdpr_request")
    private List<String> gdprRequests;
}
