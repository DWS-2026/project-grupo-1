package es.apexexpeditions.library.dto.review;

import jakarta.validation.constraints.NotNull;

public record ReviewVisibilityDTO(
        @NotNull
        Boolean hidden
) {
}
