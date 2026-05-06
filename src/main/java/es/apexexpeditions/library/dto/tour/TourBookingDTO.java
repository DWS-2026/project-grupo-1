package es.apexexpeditions.library.dto.tour;

public record TourBookingDTO(
        Long id,
        String name,
        String description,
        double price) {

}