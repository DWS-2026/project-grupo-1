package es.apexexpeditions.library.service;

import es.apexexpeditions.library.model.Review;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.repository.ReviewRepository;
import es.apexexpeditions.library.repository.TourRepository;
import es.apexexpeditions.library.repository.UserRepository;
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

    public List<Review> findByTourIdAndUserIdAndHiddenFalse(Long tourId, Long userId) {
        return reviewRepository.findByTourIdAndUserIdAndHiddenFalse(tourId, userId);
    }

    public List<Review> findVisible() {
        return reviewRepository.findByHiddenFalse();
    }

    public List<Review> findHidden() {
        return reviewRepository.findByHiddenTrue();
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    public Page<Review> findPagedByTourIdAndHiddenFalse(Long tourId, int page) {
        // Uses a fixed page size for visible reviews in the tour detail view.
        return reviewRepository.findByTourIdAndHiddenFalseOrderByCreationDateDesc(tourId, PageRequest.of(page, 3));
    }

    public double getAverageRating(Long tourId) {
        // Only visible reviews are considered in the public average.
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
        // Mirrors the same visibility rule used by the average calculation.
        return reviewRepository.findByTourIdAndHiddenFalse(tourId).size();
    }

    public long countByRating(Long tourId, int rating) {
        // Counts how many visible reviews match a specific star value.
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

        // Builds the domain object with linked user and tour before saving it.
        Review review = new Review(user, tour, rating, description);
        reviewRepository.save(review);
    }

    public Optional<Review> createReview(Long tourId, Long userId, int rating, String description) {
        Tour tour = tourRepository.findById(tourId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (tour == null || user == null) {
            return Optional.empty();
        }

        Review review = new Review(user, tour, rating, description);
        return Optional.of(reviewRepository.save(review));
    }

    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Page<Review> findPagedByUserId(Long userId, int page) {
        // Uses a larger page size for the user's personal review list.
        return reviewRepository.findByUserIdOrderByCreationDateDesc(userId, PageRequest.of(page, 5));
    }
}
