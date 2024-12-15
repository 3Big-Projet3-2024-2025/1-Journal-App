package be.helha.journalapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableJpaRepositories(
        basePackages = "be.helha.journalapp.repositories"
)
public class PostgreSQLConfig {
}
