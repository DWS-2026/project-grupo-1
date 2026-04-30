package es.apexexpeditions.library.dto.user;






// region =========== imports =================
import java.util.List;
// endregion






public record UserRequestDTO (
        String name,
        String lastName,
        String email,
        String password,
        String mainPhone,
        String secondaryPhone,
        Boolean enabled,
        Double moneySpent,
        List<String> roles
) {}
