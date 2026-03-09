package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.ReviewRepository;
import es.codeurjc.daw.library.repository.TourRepository;
import es.codeurjc.daw.library.repository.UserRepository;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            TourRepository tourRepository,
                            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    // Crear review
    @PostMapping("/tour-details/{tourId}/reviews")
    public String addReview(@PathVariable Long tourId,
                            @RequestParam int rating,
                            @RequestParam String description,
                            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Tour> optionalTour = tourRepository.findById(tourId);

        if (optionalTour.isEmpty()) {
            return "redirect:/";
        }

        Tour tour = optionalTour.get();

        // buscar usuario por email (username)
        User user = userRepository.findByEmail(principal.getName());

        if (user == null) {
            return "redirect:/";
        }

        Review review = new Review(user, tour, rating, description);
        reviewRepository.save(review);

        return "redirect:/tour-details/" + tourId;
    }

    // Borrar review
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {

        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            return "redirect:/";
        }

        Long tourId = optionalReview.get().getTour().getId();
        reviewRepository.deleteById(id);

        return "redirect:/tour-details/" + tourId;
    }

    // Editar review
    @PostMapping("/reviews/{id}/edit")
    public String editReview(@PathVariable Long id,
                             @RequestParam int rating,
                             @RequestParam String description) {

        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            return "redirect:/";
        }

        Review review = optionalReview.get();
        review.setRating(rating);
        review.setDescription(description);

        reviewRepository.save(review);

        return "redirect:/tour-details/" + review.getTour().getId();
    }
}