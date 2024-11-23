package be.helha.journalapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    private String roleName;

    // One-to-Many relationship with User (a role can be assigned to multiple users)
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<User> users;
}
