package es.apexexpeditions.library.dto.user;



// region =========== imports =================
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// endregion



// equivalent to the fuctionality available in profile page
public record PasswordUpdateDTO (
        @NotBlank @Size(max = 255) String oldPassword,
        @NotBlank @Size(min = 8, max = 255, message = "New password must be at least 8 characters") String newPassword,
        @NotBlank @Size(max = 255) String confirmPassword
) {}