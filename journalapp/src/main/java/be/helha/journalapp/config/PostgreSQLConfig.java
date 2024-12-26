package be.helha.journalapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Configuration class for setting up JPA repositories for PostgreSQL database.
 * This class enables JPA repositories scanning within the specified package.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "be.helha.journalapp.repositories"
)
public class PostgreSQLConfig {
}
