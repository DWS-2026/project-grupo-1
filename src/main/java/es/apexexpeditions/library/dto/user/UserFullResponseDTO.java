package es.apexexpeditions.library.dto.user;



// region =========== imports =================
import java.util.List;
// endregion



// equivalent to viewing all the info as admin either via the summary table or edit page
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