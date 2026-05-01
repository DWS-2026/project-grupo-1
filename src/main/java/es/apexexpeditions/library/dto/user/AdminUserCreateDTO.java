package es.apexexpeditions.library.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AdminUserCreateDTO(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        @NotBlank @Size(min = 9, max = 20) String mainPhone,
        @Size(max = 20) String secondaryPhone,
        @PositiveOrZero double moneySpent,
        boolean enabled) {
}
