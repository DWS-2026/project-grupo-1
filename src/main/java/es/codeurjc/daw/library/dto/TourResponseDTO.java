package es.codeurjc.daw.library.dto;

import java.util.List;

public record TourResponseDTO(Long id, String name, ImageDTO tourImage, String description,
        double price, int duration, int numPeople, boolean hotelIncluded,
        boolean hidden, double averageRating,
        List<Long> guideIds, List<ReviewTourDTO> reviews) {

}
