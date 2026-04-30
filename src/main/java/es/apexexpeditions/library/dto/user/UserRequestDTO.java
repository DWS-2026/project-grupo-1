package es.apexexpeditions.library.dto.user;



// region =========== imports =================
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
// endregion



public record UserRequestDTO (
        @NotBlank String name,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String mainPhone,
        String secondaryPhone,
        Boolean enabled,
        Double moneySpent,
        @NotEmpty List<String> roles // ensure a gets at least one role
) {}
