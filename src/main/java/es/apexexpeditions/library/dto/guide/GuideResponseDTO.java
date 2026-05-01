package es.apexexpeditions.library.dto.guide;

public record GuideResponseDTO(
    Long id,
    String name,
    String lastName,
    double price,
    boolean enabled,
    Long tourId,
    String tourName
) {}