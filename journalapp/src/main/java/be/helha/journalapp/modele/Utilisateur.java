package be.helha.journalapp.modele;

public class Utilisateur {
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String dateDeNaissance;
    private String email;
    private String motDePasse;
    private String nvxMotDePasse;
    private double longitude;
    private double latitude;
    private boolean isAuthorized;
    private boolean isRoleChange;
    private Role role;

    // Constructeur
    public Utilisateur(Long idUtilisateur, String nom, String prenom, String email) {
        this.idUtilisateur = idUtilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    // Getters et Setters
    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(String dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNvxMotDePasse() {
        return nvxMotDePasse;
    }

    public void setNvxMotDePasse(String nvxMotDePasse) {
        this.nvxMotDePasse = nvxMotDePasse;
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

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public boolean isRoleChange() {
        return isRoleChange;
    }

    public void setRoleChange(boolean roleChange) {
        isRoleChange = roleChange;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

