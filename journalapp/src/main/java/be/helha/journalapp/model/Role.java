package be.helha.journalapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Role")
@Data
public class Role {
    @Id
    private Long roleId;
    private String roleName;


}
