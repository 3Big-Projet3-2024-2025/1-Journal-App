package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Entity
@Table(name = "Utilisateur")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    @jakarta.persistence.Id
    private Long UserId;
    private String Last_Name;
    private String First_Name;
    private String Date_Of_Birth;
    private String Email;
    private String Password;
    private String New_Password;
    private double Longitude;
    private double Latitude;
    private boolean Is_Authorized;
    private boolean Is_Role_Change;
    @OneToOne
    private Role role;
    // Relation One-to-Many with Comment
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    // Relation One-to-Many with Newsletter
    @OneToMany( cascade = CascadeType.ALL)
    private List<Newsletter> newsletters;


}
