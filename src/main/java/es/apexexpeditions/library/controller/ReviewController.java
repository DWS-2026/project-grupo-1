package es.apexexpeditions.library.controller;

import java.security.Principal;
import java.util.Optional;

import es.apexexpeditions.library.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import es.apexexpeditions.library.model.Review;
import es.apexexpeditions.library.repository.ReviewRepository;
import es.apexexpeditions.library.service.ReviewService;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.repository.UserRepository;
import es.apexexpeditions.library.service.TourService;

@Controller
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    @Autowired // Used to create UI notifications after review actions.
    private NotificationService notificationService;
    @Autowired
    private TourService tourService;

    public ReviewController(ReviewRepository reviewRepository,
                            ReviewService reviewService, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    // Creates a new review for the selected tour.
    @PostMapping("/tour-details/{tourId}/reviews")
    public String addReview(@PathVariable Long tourId,
                            @RequestParam int rating,
                            @RequestParam String description,
                            Principal principal) {

        // Unauthenticated users must log in before posting reviews.
        if (principal == null) {
            return "redirect:/login";
        }

        String cleanDescription = Jsoup.clean(description, Safelist.relaxed());

        reviewService.addReview(tourId, principal.getName(), rating, cleanDescription);

        String tourName = tourService.findById(tourId).getName();

        // Sends a success notification with the author and related tour.
        notificationService.notify("Nueva review creada por: " + principal.getName() + " para el tour "+ tourName, "fas fa-star", "bg-success");


        return "redirect:/tour-details/" + tourId;
    }

    // Deletes a review only if it belongs to the logged-in user.
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

        // Prevents users from deleting reviews created by someone else.
        if (!review.getUser().getId().equals(user.getId())) {
            return "redirect:/review-user";
        }

        reviewRepository.deleteById(id);

        return "redirect:/review-user";
    }

    // Updates the rating and description of an existing review.
    @PostMapping("/reviews/{id}/edit")
    public String editReview(@PathVariable Long id,
                             @RequestParam int rating,
                             @RequestParam String description,
                             Principal principal) {

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

        review.setRating(rating);

        String cleanDescription = Jsoup.clean(description, Safelist.relaxed());
        review.setDescription(cleanDescription);

        reviewRepository.save(review);

        return "redirect:/review-user";
    }
}
