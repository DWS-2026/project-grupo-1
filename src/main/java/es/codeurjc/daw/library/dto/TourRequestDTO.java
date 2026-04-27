package es.codeurjc.daw.library.dto;

public record TourRequestDTO(
        String name,
        String description,
        double price,
        int duration,
        int numPeople,
        boolean hotelIncluded,
        boolean hidden) {
}
