package be.helha.journalapp.DTONEWS;
import lombok.Data;

@Data
public class Newsletterdto {
    private String title;
    private String subtitle;
    private String publicationDate;
    private long creatorId;
}
