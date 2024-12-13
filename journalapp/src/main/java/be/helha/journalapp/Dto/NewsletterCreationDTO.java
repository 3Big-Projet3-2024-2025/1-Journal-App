package be.helha.journalapp.Dto;

import lombok.Data;

@Data
public class NewsletterCreationDTO {
    private String title;
    private String subtitle;
    private String publicationDate;
    private long creatorId;
}
