package be.helha.journalapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Utilisateur")
@Data
public class User {
    @Id
    private Long userId;
    private String lastName;
    private String firstName;
    private String birthDate;
    private String email;
    private String password;
    private String newPassword;
    private double longitude;
    private double latitude;
    private boolean isAuthorized;
    private boolean isRoleChanged;
    private Role role;

    // Constructor
    public User(Long userId, String lastName, String firstName, String email) {
        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }
}
