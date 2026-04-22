package es.codeurjc.daw.library.controller.rest;

import es.codeurjc.daw.library.dto.ReviewMapper;
import es.codeurjc.daw.library.dto.ReviewRequestDTO;
import es.codeurjc.daw.library.dto.ReviewResponseDTO;
import es.codeurjc.daw.library.dto.ReviewUpdateDTO;
import es.codeurjc.daw.library.dto.ReviewVisibilityDTO;
import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.service.ReviewService;
import jakarta.validation.Valid;
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

    @Autowired
    private ReviewMapper reviewMapper;

    // Returns all reviews stored in the database.
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> listarReviews() {
        List<Review> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviewMapper.toDTOs(reviews));
    }

    // Returns all reviews marked as hidden.
    @GetMapping("/hidden")
    public ResponseEntity<List<ReviewResponseDTO>> listarReviewsOcultas() {
        List<Review> reviews = reviewService.findHidden();
        return ResponseEntity.ok(reviewMapper.toDTOs(reviews));
    }

    // Returns one review by its id, or 404 if it does not exist.
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> obtenerReview(@PathVariable Long id) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(reviewMapper.toDTO(review.get()));
    }

    // Returns all reviews written for a specific tour.
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ReviewResponseDTO>> listarReviewsPorTour(@PathVariable Long tourId) {
        List<Review> reviews = reviewService.findByTourId(tourId);
        return ResponseEntity.ok(reviewMapper.toDTOs(reviews));
    }

    // Returns all reviews written by a specific user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> listarReviewsPorUsuario(@PathVariable Long userId) {
        List<Review> reviews = reviewService.findByUserId(userId);
        return ResponseEntity.ok(reviewMapper.toDTOs(reviews));
    }

    // Creates a new review linked to an existing tour and user.
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO request) {
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

        return ResponseEntity.created(location).body(reviewMapper.toDTO(savedReview.get()));
    }

    // Deletes one review by its id, or returns 404 if it does not exist.
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable Long id) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ReviewResponseDTO deletedReview = reviewMapper.toDTO(review.get());
        reviewService.deleteById(id);
        return ResponseEntity.ok(deletedReview);
    }

    // Updates only the editable fields of a review: rating and description.
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewUpdateDTO updatedReview) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review existingReview = review.get();
        try {
            existingReview.setRating(updatedReview.rating());
            existingReview.setDescription(updatedReview.description());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }

        Review savedReview = reviewService.save(existingReview);
        return ResponseEntity.ok(reviewMapper.toDTO(savedReview));
    }

    // Updates only the visibility state of a review.
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<ReviewResponseDTO> updateReviewVisibility(@PathVariable Long id,
                                                                    @Valid @RequestBody ReviewVisibilityDTO visibility) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review existingReview = review.get();
        existingReview.setHidden(visibility.hidden());

        Review savedReview = reviewService.save(existingReview);
        return ResponseEntity.ok(reviewMapper.toDTO(savedReview));
    }
}
