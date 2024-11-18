package be.helha.journalapp.modele;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Attributs pour la connexion
    private String url;       // URL de la base de données
    private String username;  // Nom d'utilisateur
    private String password;  // Mot de passe
    private Connection connection;

    // Constructeur
    public DatabaseConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Méthode pour établir la connexion à la base de données.
     *
     * @return Connection l'objet Connection établi ou null en cas d'erreur
     */
    public Connection connect() {
        try {
            // Charger le driver JDBC (exemple pour MySQL)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Pour MySQL
            // Class.forName("org.postgresql.Driver"); // Pour PostgreSQL (si besoin)


            // Établir la connexion
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion réussie à la base de données !");
            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trouvé : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
        }
        return null; // Retourne null si la connexion échoue
    }

    /**
     * Méthode pour fermer la connexion à la base de données.
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    /**
     * Méthode pour récupérer l'état de la connexion.
     *
     * @return boolean true si connecté, false sinon.
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion : " + e.getMessage());
        }
        return false;
    }
}

