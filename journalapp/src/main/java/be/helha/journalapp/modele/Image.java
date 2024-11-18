package be.helha.journalapp.modele;

public class Image {
    private Long idImage;
    private byte[] image;

    // Constructeur
    public Image(Long idImage, byte[] image) {
        this.idImage = idImage;
        this.image = image;
    }

    // Getters et Setters
    public Long getIdImage() {
        return idImage;
    }

    public void setIdImage(Long idImage) {
        this.idImage = idImage;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
