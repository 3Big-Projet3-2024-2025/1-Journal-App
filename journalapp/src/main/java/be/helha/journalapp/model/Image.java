package be.helha.journalapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Image")
@Data
public class Image {
    @Id
    private Long imageId;
    private byte[] image;


}
