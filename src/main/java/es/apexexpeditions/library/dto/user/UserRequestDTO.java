package es.apexexpeditions.library.dto.user;




// region =========== imports =================
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
// endregion




// use: user creation via admin portal
// role req: admin
public record UserRequestDTO (
        @NotBlank @Size (max = 50) String name,
        @NotBlank @Size (max = 50) String lastName,
        @NotBlank @Email @Size (max = 255) String email,
        @NotBlank @Size (min = 8, max = 255) String password,
        @NotBlank @Size (min = 9, max = 20) String mainPhone,
        @Size (max = 20) String secondaryPhone,
        Boolean enabled,
        Double moneySpent,
        @NotEmpty List<String> roles // ensure user gets at least one role
) {}
