package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> findByTourId(Long tourId) {
        return reviewRepository.findByTourId(tourId);
    }
}
