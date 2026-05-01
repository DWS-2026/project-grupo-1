package es.apexexpeditions.library.dto.review;

public record ReviewTourDTO(
    Long id,
    int rating,
    String description,
    String creationDate
) {}