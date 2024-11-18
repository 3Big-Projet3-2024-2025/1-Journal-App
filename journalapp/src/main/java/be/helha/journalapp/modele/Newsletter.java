package be.helha.journalapp.modele;

public class Newsletter {
    private Long idNewsletter;
    private String titre;
    private String sousTitre;
    private String contenu;
    private String dateDePublication;
    private double longitude;
    private double latitude;
    private boolean isValid;
    private boolean isLue;

    // Constructeur
    public Newsletter(Long idNewsletter, String titre, String contenu, String dateDePublication) {
        this.idNewsletter = idNewsletter;
        this.titre = titre;
        this.contenu = contenu;
        this.dateDePublication = dateDePublication;
    }

    // Getters et Setters
    public Long getIdNewsletter() {
        return idNewsletter;
    }

    public void setIdNewsletter(Long idNewsletter) {
        this.idNewsletter = idNewsletter;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getDateDePublication() {
        return dateDePublication;
    }

    public void setDateDePublication(String dateDePublication) {
        this.dateDePublication = dateDePublication;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isLue() {
        return isLue;
    }

    public void setLue(boolean lue) {
        isLue = lue;
    }
}
