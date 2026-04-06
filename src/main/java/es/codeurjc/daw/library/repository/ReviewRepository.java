package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Returns all reviews linked to a tour, including hidden ones.
    List<Review> findByTourId(Long tourId);

    // Returns only visible reviews for a tour.
    List<Review> findByTourIdAndHiddenFalse(Long tourId);

    // Returns every review that is not marked as hidden.
    List<Review> findByHiddenFalse();

    // Paged version used when visible tour reviews need pagination.
    Page<Review> findByTourIdAndHiddenFalseOrderByCreationDateDesc(Long tourId, Pageable pageable);

    // Returns all reviews written by a specific user.
    List<Review> findByUserId(Long userId);

    // Paged version of the user review query.
    Page<Review> findByUserIdOrderByCreationDateDesc(Long userId, Pageable pageable);
}
