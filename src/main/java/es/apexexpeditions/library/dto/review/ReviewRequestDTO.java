package es.apexexpeditions.library.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDTO(
        @NotNull
        Long tourId,

        @NotNull
        Long userId,

        @Min(1)
        @Max(5)
        int rating,

        @NotBlank
        String description
) {
}
