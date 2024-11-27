package be.helha.journalapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String lastName;
    private String firstName;
    private String dateOfBirth;
    private String email;
    private String password;
    private String newPassword;
    private double longitude;
    private double latitude;
    private boolean isAuthorized;
    private boolean isRoleChange;

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
}
