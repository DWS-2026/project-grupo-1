package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Esto te permitirá buscar usuarios por email más adelante
    User findByEmail(String email);
}