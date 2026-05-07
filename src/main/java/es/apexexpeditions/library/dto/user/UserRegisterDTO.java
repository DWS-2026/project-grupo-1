package es.apexexpeditions.library.dto.user;




// region =========== imports =================
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// endregion




// use: self register
// role req: user
public record UserRegisterDTO (
        @NotBlank (message = "Name is required") @Size (max = 50) String name,
        @NotBlank (message = "Last name is required") @Size (max = 50) String lastName,
        @NotBlank @Email (message = "Invalid email format") @Size (max = 255) String email,
        @NotBlank @Size (min = 8, max = 255, message = "Password must be at least 8 characters") String password,
        @NotBlank @Size (min = 9, max = 20) String mainPhone,
        @Size (max = 20) String secondaryPhone,
        @AssertTrue (message = "You must accept terms") boolean termsAccepted
) {}
