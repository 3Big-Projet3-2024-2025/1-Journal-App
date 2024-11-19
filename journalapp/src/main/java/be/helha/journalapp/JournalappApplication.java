package be.helha.journalapp;

import be.helha.journalapp.model.Comment;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JournalappApplication {

	public static void main(String[] args) {
/*		// Configuration de la base de données
		String url = "jdbc:mysql://localhost:3306/journalapp"; // Exemple pour MySQL
		String username = "root";
		String password = "";

		// Création de l'objet DatabaseConnection
		DatabaseConnection dbConnection = new DatabaseConnection(url, username, password);

		// Connexion à la base de données
		Connection conn = dbConnection.connect();

		// Vérifier si la connexion est réussie
		if (conn != null) {
			System.out.println("Connexion établie avec succès !");
		} else {
			System.out.println("Échec de la connexion à la base de données.");
		}

		// Déconnexion de la base de données
		dbConnection.disconnect();
		SpringApplication.run(JournalappApplication.class, args);

 */



	}
	@GetMapping("/hello")
	public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

}
