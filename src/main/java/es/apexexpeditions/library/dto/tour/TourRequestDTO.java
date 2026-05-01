package es.apexexpeditions.library.dto.tour;

public record TourRequestDTO(
        String name,
        String description,
        double price,
        int duration,
        int numPeople,
        boolean hotelIncluded,
        boolean hidden) {
}
