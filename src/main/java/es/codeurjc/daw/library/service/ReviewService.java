package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
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

    public void deleteById(Long id){
        reviewRepository.deleteById(id);
    };

}
