package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.ReviewRepository;
import es.codeurjc.daw.library.repository.TourRepository;
import es.codeurjc.daw.library.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         TourRepository tourRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> findByTourId(Long tourId) {
        return reviewRepository.findByTourId(tourId);
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findByTourIdAndHiddenFalse(Long tourId) {
        return reviewRepository.findByTourIdAndHiddenFalse(tourId);
    }

    public List<Review> findVisible() {
        return reviewRepository.findByHiddenFalse();
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    public Page<Review> findPagedByTourIdAndHiddenFalse(Long tourId, int page) {
        return reviewRepository.findByTourIdAndHiddenFalse(tourId, PageRequest.of(page, 3));
    }

    public double getAverageRating(Long tourId) {
        List<Review> reviews = reviewRepository.findByTourIdAndHiddenFalse(tourId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }

        return sum / reviews.size();
    }

    public int getTotalReviews(Long tourId) {
        return reviewRepository.findByTourIdAndHiddenFalse(tourId).size();
    }

    public long countByRating(Long tourId, int rating) {
        return reviewRepository.findByTourIdAndHiddenFalse(tourId)
                .stream()
                .filter(review -> review.getRating() == rating)
                .count();
    }

    public void addReview(Long tourId, String email, int rating, String description) {
        Tour tour = tourRepository.findById(tourId).orElse(null);
        User user = userRepository.findByEmail(email);

        if (tour == null || user == null) {
            throw new RuntimeException("Tour o usuario no encontrado");
        }

        Review review = new Review(user, tour, rating, description);
        reviewRepository.save(review);
    }
}