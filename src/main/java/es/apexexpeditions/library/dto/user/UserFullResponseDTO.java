package es.apexexpeditions.library.dto.user;




// region =========== imports =================
import java.util.List;
// endregion




// use: show all stored user info on a specific user
// role req: admin
public record UserFullResponseDTO (
        Long id,
        String name,
        String lastName,
        String email,
        String mainPhone,
        String secondaryPhone,
        boolean enabled,
        String creationDate,
        double moneySpent,
        List<String> roles,
        Long imageId
) {}