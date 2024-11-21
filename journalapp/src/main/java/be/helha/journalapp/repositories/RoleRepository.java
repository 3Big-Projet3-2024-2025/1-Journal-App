package be.helha.journalapp.repositories;

import be.helha.journalapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a Role by its name.
     *
     * @param roleName The name of the role.
     * @return An Optional containing the role if found, or empty if not.
     */
    Optional<Role> findByRoleName(String roleName);
}
