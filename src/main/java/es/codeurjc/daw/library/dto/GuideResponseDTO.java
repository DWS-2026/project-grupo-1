package es.codeurjc.daw.library.dto;

public record GuideResponseDTO(
    Long id,
    String name,
    String lastName,
    double price,
    boolean enabled,
    Long tourId,
    String tourName
) {}