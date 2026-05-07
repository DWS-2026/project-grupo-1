package es.apexexpeditions.library.repository;

import es.apexexpeditions.library.model.Image;
// region =========== imports =================
import es.apexexpeditions.library.model.User; // to be able to work with user entity

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository; 

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMainPhone(String phone);

    boolean existsBySecondaryPhone(String phone);

    List<User> findByProfilePicture(Image profilePicture);
}