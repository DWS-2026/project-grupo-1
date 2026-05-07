package es.apexexpeditions.library.dto.user;




// region =========== imports =================
import es.apexexpeditions.library.model.User;
import org.springframework.stereotype.Component;
// endregion




@Component
public class UserMapper {
    // =========== 1. toBasicDTO =================
    // show brief summary of key user info
    public UserBasicResponseDTO toBasicDTO (User user) {
        Long imgId = (user.getProfilePicture() != null) ? user.getProfilePicture().getId() : null;
        return new UserBasicResponseDTO (
                user.getId(), user.getName(), user.getLastName(),
                user.getEmail(), user.getMainPhone(), user.isEnabled(), imgId
        );
    }
    // endregion


    // =========== 2. toFullDTO =================
    // show all stored user info on a specific user
    public UserFullResponseDTO toFullDTO (User user) {
        Long imgId = (user.getProfilePicture() != null) ? user.getProfilePicture().getId() : null;
        return new UserFullResponseDTO (
                user.getId(), user.getName(), user.getLastName(), user.getEmail(),
                user.getMainPhone(), user.getSecondaryPhone(), user.isEnabled(),
                user.getFormattedCreationDate(),
                user.getMoneySpent(), user.getRoles(), imgId, user.getMedicalCertificateName()
        );
    }
    // endregion
}