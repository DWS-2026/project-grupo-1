package es.codeurjc.daw.library.dto;






// region =========== imports =================
import es.codeurjc.daw.library.model.User;
import org.springframework.stereotype.Component;
// endregion






@Component
public class UserMapper {
    public UserResponseDTO toDTO (User user) {
        Long imgId = (user.getProfilePicture() != null) ? user.getProfilePicture().getId() : null;
        return new UserResponseDTO(
                user.getId(), user.getName(), user.getLastName(), user.getEmail(), user.getRoles(), imgId
        );
    }
}