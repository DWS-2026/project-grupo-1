package es.codeurjc.daw.library.dto;






// region =========== imports =================
import java.util.List;
// endregion






public record UserResponseDTO (
        Long id,
        String name,
        String lastName,
        String email,
        List<String> roles
) {}