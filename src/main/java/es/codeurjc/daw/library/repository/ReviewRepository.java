package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTourId(Long tourId);

    List<Review> findByTourIdAndHiddenFalse(Long tourId);

    List<Review> findByHiddenFalse();

    Page<Review> findByTourIdAndHiddenFalse(Long tourId, Pageable pageable);
}
