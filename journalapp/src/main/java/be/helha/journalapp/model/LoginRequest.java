package be.helha.journalapp.model;

public class LoginRequest {

    private String username;
    private String password;

    // Constructeur par défaut (si nécessaire)
    public LoginRequest() {
    }

    // Constructeur avec arguments
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters et setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
