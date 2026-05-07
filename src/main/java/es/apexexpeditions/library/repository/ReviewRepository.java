package es.apexexpeditions.library.repository;

import es.apexexpeditions.library.model.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Returns all reviews linked to a tour, including hidden ones.
    List<Review> findByTourId(Long tourId);

    // Returns only visible reviews for a tour.
    List<Review> findByTourIdAndHiddenFalse(Long tourId);

    // Paged version of visible reviews for a tour.
    Page<Review> findByTourIdAndHiddenFalse(Long tourId, Pageable pageable);

    // Returns only visible reviews linked to both a tour and a user.
    List<Review> findByTourIdAndUserIdAndHiddenFalse(Long tourId, Long userId);

    // Paged version of visible reviews linked to both a tour and a user.
    Page<Review> findByTourIdAndUserIdAndHiddenFalse(Long tourId, Long userId, Pageable pageable);

    // Returns every review that is not marked as hidden.
    List<Review> findByHiddenFalse();

    // Paged version of visible reviews.
    Page<Review> findByHiddenFalse(Pageable pageable);

    // Returns every review marked as hidden.
    List<Review> findByHiddenTrue();

    // Paged version of hidden reviews.
    Page<Review> findByHiddenTrue(Pageable pageable);

    // Paged version used when visible tour reviews need pagination.
    Page<Review> findByTourIdAndHiddenFalseOrderByCreationDateDesc(Long tourId, Pageable pageable);

    // Returns all reviews written by a specific user.
    List<Review> findByUserId(Long userId);

    // Returns only visible reviews written by a specific user.
    Page<Review> findByUserIdAndHiddenFalse(Long userId, Pageable pageable);

    // Paged version of the user review query.
    Page<Review> findByUserIdOrderByCreationDateDesc(Long userId, Pageable pageable);
}
