package es.apexexpeditions.library.dto.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record GuideRequestDTO(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    String name,
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede tener más de 100 caracteres")
    String lastName,
    
    @Positive(message = "El precio debe ser positivo")
    double price,
    
    Long tourId,
    
    boolean enabled
) {}