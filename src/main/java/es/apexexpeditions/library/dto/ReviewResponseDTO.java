package es.apexexpeditions.library.dto;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        Long tourId,
        Long userId,
        int rating,
        String description,
        boolean hidden,
        LocalDateTime creationDate
) {
}
