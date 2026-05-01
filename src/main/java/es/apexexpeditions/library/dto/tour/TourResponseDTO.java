package es.apexexpeditions.library.dto.tour;

import java.util.List;

import es.apexexpeditions.library.dto.image.ImageDTO;
import es.apexexpeditions.library.dto.review.ReviewTourDTO;

public record TourResponseDTO(Long id, String name, ImageDTO tourImage, String description,
        double price, int duration, int numPeople, boolean hotelIncluded,
        boolean hidden, double averageRating,
        List<Long> guideIds, List<ReviewTourDTO> reviews) {

}
