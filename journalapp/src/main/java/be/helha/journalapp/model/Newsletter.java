package be.helha.journalapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Newsletter")
@Data
public class Newsletter {
    @Id
    private Long newsletterId;
    private String title;
    private String subtitle;
    private String content;
    private String publicationDate;
    private double longitude;
    private double latitude;
    private boolean isValid;
    private boolean isRead;


}
