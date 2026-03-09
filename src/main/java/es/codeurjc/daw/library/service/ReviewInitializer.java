package es.codeurjc.daw.library.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.ReviewRepository;
import es.codeurjc.daw.library.repository.TourRepository;
import es.codeurjc.daw.library.repository.UserRepository;

@Component
@Order(4)
public class ReviewInitializer implements CommandLineRunner {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository;

    @Override
    public void run(String... args) {

        if (reviewRepository.count() != 0) return;

        User user = userRepository.findAll().get(0);
        Tour tour = tourRepository.findAll().get(0);

        createReview(user, tour, 3, "Una experiencia increíble");
        createReview(user, tour, 5, "Una locura ");
        createReview(user, tour, 2, " locura ");








        System.out.println(">>> Reviews initialized");
    }

    private void createReview(User user, Tour tour,
                              int rating, String description) {

        Review review = new Review();
        review.setUser(user);
        review.setTour(tour);
        review.setRating(rating);
        review.setDescription(description);

        reviewRepository.save(review);
    }
}

