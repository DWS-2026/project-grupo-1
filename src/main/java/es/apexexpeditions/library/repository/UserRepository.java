package es.apexexpeditions.library.repository;






// region =========== imports =================
import es.apexexpeditions.library.model.User; // to be able to work with user entity
import org.springframework.data.jpa.repository.JpaRepository; // to inherit crud operations
// endregion






public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail (String email);
    boolean existsByEmail (String email);
    boolean existsByMainPhone (String phone);
    boolean existsBySecondaryPhone (String phone);
}