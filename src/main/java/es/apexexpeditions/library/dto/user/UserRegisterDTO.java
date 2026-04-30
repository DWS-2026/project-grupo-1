package es.apexexpeditions.library.dto.user;



// region =========== imports =================
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// endregion



public record UserRegisterDTO (
        @NotBlank (message = "Name is required") String name,
        @NotBlank (message = "Last name is required") String lastName,
        @NotBlank @Email (message = "Invalid email format") String email,
        @NotBlank @Size (min = 8, message = "Password must be at least 8 characters") String password,
        @NotBlank String mainPhone,
        String secondaryPhone,
        @AssertTrue(message = "You must accept terms") boolean termsAccepted
) {}
