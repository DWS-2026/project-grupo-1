package es.apexexpeditions.library.dto.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record GuideRequestDTO(
    @NotBlank(message = "El nombre es obligatorio")
    String name,
    
    @NotBlank(message = "El apellido es obligatorio")
    String lastName,
    
    @Positive(message = "El precio debe ser positivo")
    double price,
    
    Long tourId,
    
    boolean enabled
) {}