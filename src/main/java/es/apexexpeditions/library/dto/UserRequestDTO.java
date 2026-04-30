package es.apexexpeditions.library.dto;






// region =========== imports =================
import java.util.List;
// endregion






public record UserRequestDTO (
        String name,
        String lastName,
        String email,
        String password, // Solo para entrada
        String mainPhone,
        List<String> roles
) {}
