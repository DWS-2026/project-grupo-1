package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // to search users by email (not used yet)
    User findByEmail(String email);
}