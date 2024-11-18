package be.helha.journalapp.modele;

public class Role {
    private Long idRole;
    private String nomDuRole;

    // Constructeur
    public Role(Long idRole, String nomDuRole) {
        this.idRole = idRole;
        this.nomDuRole = nomDuRole;
    }

    // Getters et Setters
    public Long getIdRole() {
        return idRole;
    }

    public void setIdRole(Long idRole) {
        this.idRole = idRole;
    }

    public String getNomDuRole() {
        return nomDuRole;
    }

    public void setNomDuRole(String nomDuRole) {
        this.nomDuRole = nomDuRole;
    }
}

