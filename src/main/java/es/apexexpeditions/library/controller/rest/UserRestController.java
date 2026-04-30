package es.apexexpeditions.library.controller.rest;






// region =========== imports =================
import es.apexexpeditions.library.dto.user.*;
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.ImageService;
import es.apexexpeditions.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.awt.Color;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
// endregion





/**
 * API REST v1 - User and Profile Management
 * Based on specifications from funcionalidades_api.txt:
 * * --- ADMINISTRATOR FUNCTIONALITIES ---
 * - GET    /api/v1/users            : retrieve paginated user list (Summary view: name, last names, email, main phone, status, imageId).
 * - GET    /api/v1/users/{id}       : retrieve full details of a specific user (Includes secondary phone, registration date, money spent, and roles).
 * - POST   /api/v1/users/           : create a new user (Allows defining status, phone numbers, money, and roles. Generates a default avatar).
 * - PUT    /api/v1/users/{id}/image : update/Upload profile image for any user via their ID.
 * - PUT    /api/v1/users/me         : update own profile (name, lastName, phones)
 * - DELETE /api/v1/users/{id}       : remove a user from the system.
 * * --- USER FUNCTIONALITIES (SELF) ---
 * - GET    /api/v1/users/me          : view full profile of the currently authenticated user (Name, phone, image, etc.).
 * - PUT    /api/v1/users/{id}        : admin update of any user (all fields, including email and status)
 * - PUT    /api/v1/users/me/password : update password (Requires old password validation and new password confirmation).
 * * Note: all responses including image return an 'imageId'.
 * The binary resource is retrieved via: GET /api/v1/images/{imageId}/media
 */
@RestController
@RequestMapping ("/api/v1/users")
public class UserRestController {
    // region =========== autowired =================
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ImageService imageService;
    // endregion



    // region =========== GetMapping =================
    // region 1. getUsers
    // overview of all users
    @GetMapping ({"", "/"})
    public Page<UserBasicResponseDTO> getUsers (Pageable pageable) {
        return userService.findAll(pageable).map (userMapper::toBasicDTO);
    }
    // endregion

    // region 2. getUser
    // full details of a single user
    @GetMapping ("/{id}")
    public ResponseEntity<UserFullResponseDTO> getUser (@PathVariable Long id) {
        User user = userService.findById (id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok (userMapper.toFullDTO (user));
    }
    // endregion

    // region 3. getMyProfile
    // shows the users profile page info
    @GetMapping("/me")
    public ResponseEntity<UserFullResponseDTO> getMyProfile() {
        User user = userService.getLoggedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(userMapper.toFullDTO(user));
    }
    // endregion

    // 4. stats
    // admin endpoint to get general user statistics
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getUserStats() {
        User loggedUser = userService.getLoggedUser();

        if (loggedUser == null || !userService.isAdmin(loggedUser)) {   // check auth
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userService.getUserStats());
    }
    // endregion
    // endregion



    // region =========== PutMapping =================
    // region 1. updateMyPassword
    // allows the user to change their password
    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword (@RequestBody PasswordUpdateDTO passwords) {
        User user = userService.getLoggedUser();
        if (user == null) return ResponseEntity.status (HttpStatus.UNAUTHORIZED).build();

        if (!passwords.newPassword().equals (passwords.confirmPassword())) {
            return ResponseEntity.badRequest().build();
        }
        if (!passwordEncoder.matches (passwords.oldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        user.setPassword (passwordEncoder.encode (passwords.newPassword()));
        userService.save (user);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 2. updateImage
    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) throws IOException {
        User user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();

        if (user.getProfilePicture() != null) {
            imageService.replaceImageFile(user.getProfilePicture().getId(), imageFile);
        } else {
            user.setProfilePicture(imageService.createImage(imageFile));
            userService.save(user);
        }
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 3. updateMyProfile
    // allows user to update itself (on non restricted fields)
    @PutMapping("/me")
    public ResponseEntity<UserFullResponseDTO> updateMyProfile(@RequestBody UserUpdateDTO updateData) {
        User user = userService.getLoggedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // filter out restricted files
        if (updateData.name() != null) user.setName(updateData.name());
        if (updateData.lastName() != null) user.setLastName(updateData.lastName());
        if (updateData.mainPhone() != null) user.setMainPhone(updateData.mainPhone());
        if (updateData.secondaryPhone() != null) user.setSecondaryPhone(updateData.secondaryPhone());

        userService.save(user);
        return ResponseEntity.ok(userMapper.toFullDTO(user));
    }
    // endregion

    // region 4. updateUser
    // admin endpoint to modify any info on any user
    @PutMapping("/{id}")
    public ResponseEntity<UserFullResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO updateData) {

        User user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();

        // admin-editing exclusive attributes
        if (updateData.email() != null) user.setEmail(updateData.email());
        if (updateData.enabled() != null) user.setEnabled(updateData.enabled());

        // non exclusive attributes
        if (updateData.name() != null) user.setName(updateData.name());
        if (updateData.lastName() != null) user.setLastName(updateData.lastName());
        if (updateData.mainPhone() != null) user.setMainPhone(updateData.mainPhone());
        if (updateData.secondaryPhone() != null) user.setSecondaryPhone(updateData.secondaryPhone());
        if (updateData.moneySpent() != null) user.setMoneySpent(updateData.moneySpent());
        if (updateData.roles() != null) user.setRoles(updateData.roles());

        userService.save(user);
        return ResponseEntity.ok(userMapper.toFullDTO(user));
    }
    // endregion

    // region 5. updateMyImage
    // allows users to update their own pfp
    public ResponseEntity<Void> updateMyImage(@RequestParam MultipartFile imageFile) throws IOException {
        User loggedUser = userService.getLoggedUser();
        if (loggedUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (loggedUser.getProfilePicture() != null) {
            imageService.replaceImageFile(loggedUser.getProfilePicture().getId(), imageFile);
        } else {
            loggedUser.setProfilePicture(imageService.createImage(imageFile));
            userService.save(loggedUser);
        }
        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion



    // region =========== PostMapping =================
    // region 1. createUser
    @PostMapping({"", "/"})
    public ResponseEntity<UserFullResponseDTO> createUser(@RequestBody UserRequestDTO req) {
        User user = new User(req.name(), req.lastName(), req.email(),
                passwordEncoder.encode(req.password()), req.mainPhone(), req.secondaryPhone());

        user.setRoles(req.roles());
        if (req.enabled() != null) user.setEnabled(req.enabled());
        if (req.moneySpent() != null) user.setMoneySpent(req.moneySpent());

        // Generar avatar por defecto (el admin podrá subir imagen luego por el PUT de imagen)
        byte[] avatar = userService.generateDefaultAvatar("Usuario", user.getName(), new Color(13, 110, 253));
        user.setProfilePicture(new Image(avatar));

        userService.save(user);
        return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId())).body(userMapper.toFullDTO(user));
    }
    // endregion
    // endregion



    // region =========== DeleteMapping =================
    // region 1. deleteUser
    // used by admins to delete any non-user admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User targetUser = userService.findById(id);
        if (targetUser == null) {   // check user exists
            return ResponseEntity.notFound().build();
        }
        if (userService.isAdmin(targetUser)) {   // prevent deletion of admins
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.delete(targetUser);  // allow deletion of standard users
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 2. deleteMyAccount
    // used by non-admins to delete their own account
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        User loggedUser = userService.getLoggedUser();
        if (loggedUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (userService.isAdmin(loggedUser)) {   // prevent admin self-deletion
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.delete(loggedUser);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 3. deleteMyImage
    // allows users to delete their own pfp
    @DeleteMapping ("/me/image")
    public ResponseEntity<Void> deleteMyImage() {
        User loggedUser = userService.getLoggedUser();
        if (loggedUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (loggedUser.getProfilePicture() != null) {
            long imageId = loggedUser.getProfilePicture().getId();
            loggedUser.setProfilePicture(null);
            userService.save(loggedUser); // save user first to break foreign key relationship
            imageService.deleteImage(imageId); // physically delete image entity
        }

        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion

    // region =========== PatchMapping =================
    // region 1. toggleUserStatus
    // admin endpoint to quickly enable/disable user without sending a full DTO
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long id) {
        User loggedUser = userService.getLoggedUser();
        User targetUser = userService.findById(id);

        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Prevent admin from disabling themselves (avoids accidental lockouts)
        if (loggedUser != null && loggedUser.getId().equals(targetUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // flip the status
        targetUser.setEnabled(!targetUser.isEnabled());
        userService.save(targetUser);

        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion
}