package es.codeurjc.daw.library.controller.rest;

import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // Returns all reviews stored in the database.
    @GetMapping
    public ResponseEntity<List<Review>> listarReviews() {
        List<Review> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    // Returns all reviews marked as hidden.
    @GetMapping("/hidden")
    public ResponseEntity<List<Review>> listarReviewsOcultas() {
        List<Review> reviews = reviewService.findHidden();
        return ResponseEntity.ok(reviews);
    }

    // Returns one review by its id, or 404 if it does not exist.
    @GetMapping("/{id}")
    public ResponseEntity<Review> obtenerReview(@PathVariable Long id) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(review.get());
    }

    // Returns all reviews written for a specific tour.
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<Review>> listarReviewsPorTour(@PathVariable Long tourId) {
        List<Review> reviews = reviewService.findByTourId(tourId);
        return ResponseEntity.ok(reviews);
    }

    // Returns all reviews written by a specific user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> listarReviewsPorUsuario(@PathVariable Long userId) {
        List<Review> reviews = reviewService.findByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    // Creates a new review linked to an existing tour and user.
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody CreateReviewRequest request) {
        Optional<Review> savedReview;

        try {
            savedReview = reviewService.createReview(
                    request.tourId(),
                    request.userId(),
                    request.rating(),
                    request.description()
            );
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }

        if (savedReview.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedReview.get().getId())
                .toUri();

        return ResponseEntity.created(location).body(savedReview.get());
    }

    // Deletes one review by its id, or returns 404 if it does not exist.
    @DeleteMapping("/{id}")
    public ResponseEntity<Review> deleteReview(@PathVariable Long id) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        reviewService.deleteById(id);
        return ResponseEntity.ok(review.get());
    }

    // Updates only the editable fields of a review: rating and description.
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review existingReview = review.get();
        existingReview.setRating(updatedReview.getRating());
        existingReview.setDescription(updatedReview.getDescription());

        Review savedReview = reviewService.save(existingReview);
        return ResponseEntity.ok(savedReview);
    }

    public record CreateReviewRequest(
            Long tourId,
            Long userId,
            int rating,
            String description
    ) {
    }
}
