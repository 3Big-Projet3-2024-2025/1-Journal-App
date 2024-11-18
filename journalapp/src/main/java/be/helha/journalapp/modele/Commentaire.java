package be.helha.journalapp.modele;

public class Commentaire {
    private Long idCommentaire;
    private String contenue;
    private String dateDePublication;

    // Constructeur
    public Commentaire(Long idCommentaire, String contenue, String dateDePublication) {
        this.idCommentaire = idCommentaire;
        this.contenue = contenue;
        this.dateDePublication = dateDePublication;
    }

    // Getters et Setters
    public Long getIdCommentaire() {
        return idCommentaire;
    }

    public void setIdCommentaire(Long idCommentaire) {
        this.idCommentaire = idCommentaire;
    }

    public String getContenue() {
        return contenue;
    }

    public void setContenue(String contenue) {
        this.contenue = contenue;
    }

    public String getDateDePublication() {
        return dateDePublication;
    }

    public void setDateDePublication(String dateDePublication) {
        this.dateDePublication = dateDePublication;
    }
}

