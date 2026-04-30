package es.apexexpeditions.library.dto.user;



// region =========== imports =================
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
// endregion



// dto shared between user and admin, applies restriction filters by role
public record UserUpdateDTO (
        @Size(max = 50) String name,
        @Size(max = 50) String lastName,
        @Email String email,          // admin exclusive
        String mainPhone,
        String secondaryPhone,
        Boolean enabled,       // admin exclusive
        @PositiveOrZero Double moneySpent,     // admin exclusive
        List<String> roles     // admin exclusive
) {}
