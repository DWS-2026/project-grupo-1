package es.codeurjc.daw.library.dto;

public record ReviewTourDTO(
    Long id,
    int rating,
    String description,
    String creationDate
) {}