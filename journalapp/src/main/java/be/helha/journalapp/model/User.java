package be.helha.journalapp.model;

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

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Newsletter> newsletters;
}
