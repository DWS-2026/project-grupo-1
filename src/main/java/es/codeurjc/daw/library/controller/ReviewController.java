package es.codeurjc.daw.library.controller;

import java.security.Principal;
import java.util.Optional;

import es.codeurjc.daw.library.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.repository.ReviewRepository;
import es.codeurjc.daw.library.service.ReviewService;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import es.codeurjc.daw.library.service.TourService;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    @Autowired // to generate notifications
    private NotificationService notificationService;
    @Autowired
    private  TourService tourService;
    public ReviewController(ReviewRepository reviewRepository,
                            ReviewService reviewService, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
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

        reviewService.addReview(tourId, principal.getName(), rating, description);

        String tourName = tourService.findById(tourId).getName();

        // ajustar para que notifique
        notificationService.notify("Nueva review creada por: " + principal.getName() + " para el tour "+ tourName, "fas fa-star", "bg-success");


        return "redirect:/tour-details/" + tourId;
    }

    // Borrar review
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/";
        }

        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            return "redirect:/review-user";
        }

        Review review = optionalReview.get();

        if (!review.getUser().getId().equals(user.getId())) {
            return "redirect:/review-user";
        }

        reviewRepository.deleteById(id);

        return "redirect:/review-user";
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