package es.codeurjc.daw.library.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewVisibilityDTO(
        @NotNull
        Boolean hidden
) {
}
