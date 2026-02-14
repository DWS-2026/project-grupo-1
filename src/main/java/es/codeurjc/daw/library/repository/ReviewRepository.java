package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
