package es.apexexpeditions.library.controller.rest;

import es.apexexpeditions.library.dto.review.ReviewMapper;
import es.apexexpeditions.library.dto.review.ReviewRequestDTO;
import es.apexexpeditions.library.dto.review.ReviewResponseDTO;
import es.apexexpeditions.library.dto.review.ReviewUpdateDTO;
import es.apexexpeditions.library.dto.review.ReviewVisibilityDTO;
import es.apexexpeditions.library.model.Review;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.ReviewService;
import es.apexexpeditions.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserService userService;

    // Returns all visible reviews stored in the database.
    @GetMapping
    public ResponseEntity<Collection<ReviewResponseDTO>> listarReviews() {
        List<Review> reviews = reviewService.findVisible();
        return ResponseEntity.ok(toDTOs(reviews));
    }

    // Returns all reviews marked as hidden.
    @GetMapping("/hidden")
    public ResponseEntity<Collection<ReviewResponseDTO>> listarReviewsOcultas() {
        List<Review> reviews = reviewService.findHidden();
        return ResponseEntity.ok(toDTOs(reviews));
    }

    // Returns one review by its id, or 404 if it does not exist.
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> obtenerReview(@PathVariable Long id) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review foundReview = review.get();

        if (foundReview.isHidden()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDTO(foundReview));
    }

    // Returns all visible reviews written for a specific tour.
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<Collection<ReviewResponseDTO>> listarReviewsPorTour(@PathVariable Long tourId) {
        List<Review> reviews = reviewService.findByTourIdAndHiddenFalse(tourId);
        return ResponseEntity.ok(toDTOs(reviews));
    }

    // Returns all visible reviews written by a specific user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<Collection<ReviewResponseDTO>> listarReviewsPorUsuario(@PathVariable Long userId) {
        List<Review> reviews = reviewService.findByUserId(userId).stream()
                .filter(review -> !review.isHidden())
                .toList();
        return ResponseEntity.ok(toDTOs(reviews));
    }

    // Creates a new review linked to an existing tour and the authenticated user.
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO request,
                                                          Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        User loggedUser = userService.findByEmail(principal.getName());

        if (loggedUser == null) {
            return ResponseEntity.status(401).build();
        }

        if (!isAdmin(loggedUser) && !loggedUser.getId().equals(request.userId())) {
            return ResponseEntity.status(403).build();
        }

        Long reviewUserId = isAdmin(loggedUser) ? request.userId() : loggedUser.getId();
        Optional<Review> savedReview;
        Review reviewRequest = toDomain(request);

       // 1. Sanitizamos el HTML de la descripción
        String cleanDescription = Jsoup.clean(request.description(), Safelist.relaxed());

        try {
            // 2. Llamamos al servicio con los 4 parámetros estándar,
            // pero le enviamos directamente la descripción LIMPIA (cleanDescription)
            savedReview = reviewService.createReview(
                    request.tourId(),
                    reviewUserId,
                    request.rating(),
                    cleanDescription // <-- Le pasamos la versión segura en el campo description
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

        return ResponseEntity.created(location).body(toDTO(savedReview.get()));
    }

    // Deletes one review by its id, or returns 404 if it does not exist.
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable Long id,
                                                          Authentication authentication) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review existingReview = review.get();

        if (!canManageReview(existingReview, authentication)) {
            return ResponseEntity.status(403).build();
        }

        ReviewResponseDTO deletedReview = toDTO(existingReview);
        reviewService.deleteById(id);
        return ResponseEntity.ok(deletedReview);
    }

    // Updates only the editable fields of a review: rating and description.
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long id,
                                                          @Valid @RequestBody ReviewUpdateDTO updatedReview,
                                                          Authentication authentication) {
        Optional<Review> review = reviewService.findById(id);

        if (review.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Review existingReview = review.get();

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        if (!canManageReview(existingReview, authentication)) {
            return ResponseEntity.status(403).build();
        }

        try {
            existingReview.setRating(updatedReview.rating());
            String cleanDescription = Jsoup.clean(updatedReview.description(), Safelist.relaxed());
            existingReview.setDescription(cleanDescription); 
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        }

        Review savedReview = reviewService.save(existingReview);
        return ResponseEntity.ok(toDTO(savedReview));
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
        return ResponseEntity.ok(toDTO(savedReview));
    }

    private ReviewResponseDTO toDTO(Review review) {
        return reviewMapper.toDTO(review);
    }

    private Review toDomain(ReviewRequestDTO reviewDTO) {
        return reviewMapper.toDomain(reviewDTO);
    }

    private Collection<ReviewResponseDTO> toDTOs(Collection<Review> reviews) {
        return reviewMapper.toDTOs(reviews);
    }

    private boolean canManageReview(Review review, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        return review.getUser().getEmail().equals(authentication.getName());
    }

    private boolean isAdmin(User user) {
        return user.getRoles() != null && user.getRoles().contains("ADMIN");
    }
}
