package es.apexexpeditions.library.dto.user;




// region =========== imports =================
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.List;
// endregion




// use: update a users info
// can be used both by admin or user (controller restricts specific fields)
// req: user (or admin for all fields)
public record UserUpdateDTO (
        @Size(max = 50) String name,
        @Size(max = 50) String lastName,
        @Email @Size(max = 255) String email,          // admin exclusive
        @Size(min = 9, max = 20) String mainPhone,
        @Size(max = 20) String secondaryPhone,
        Boolean enabled,                               // admin exclusive
        @PositiveOrZero Double moneySpent,             // admin exclusive
        List<String> roles                             // admin exclusive
) {}
