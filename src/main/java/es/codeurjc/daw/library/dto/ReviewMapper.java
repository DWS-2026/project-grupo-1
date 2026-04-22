package es.codeurjc.daw.library.dto;

import es.codeurjc.daw.library.model.Review;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {

    public ReviewResponseDTO toDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getTour().getId(),
                review.getUser().getId(),
                review.getRating(),
                review.getDescription(),
                review.isHidden(),
                review.getCreationDate()
        );
    }

    public List<ReviewResponseDTO> toDTOs(List<Review> reviews) {
        return reviews.stream()
                .map(this::toDTO)
                .toList();
    }
}
