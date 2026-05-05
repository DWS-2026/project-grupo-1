package es.apexexpeditions.library.controller.rest;




// region =========== imports =================
import es.apexexpeditions.library.dto.user.*;
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.ImageService;
import es.apexexpeditions.library.service.NotificationService;
import es.apexexpeditions.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
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
// endregion




/**
 * API REST v1: user management
 * * --- SECURITY AND ACCESS STRUCTURE ---
 * - ADMIN: full management access, statistics, and modification of sensitive fields
 * - OWNER: access to their own information and limited profile editing
 * - PUBLIC: new user registration
 *
 * --- ADMINISTRATOR ENDPOINTS (third-party management) ---
 * - GET    /api/v1/users            : paginated user list (uses: UserBasicResponseDTO)
 * - GET    /api/v1/users/{id}       : full user details (uses: UserFullResponseDTO). Req: admin or owner
 * - GET    /api/v1/users/stats      : global system statistics (uses: UserStatsDTO). Req: admin
 * - POST   /api/v1/users/           : manual user creation (uses: UserRequestDTO). Req: admin. Generates default avatar
 * - PUT    /api/v1/users/{id}       : user update (uses: UserUpdateDTO)
 *   - If admin: modifies all fields (email, roles, moneySpent, enabled)
 *   - If owner: only basic fields (name, lastName, phones)
 * - PUT    /api/v1/users/{id}/image : updates any user's profile image via MultipartFile
 * - PATCH  /api/v1/users/{id}/status: toggles a user's status (enabled). Req: admin (self-toggle prohibited)
 * - DELETE /api/v1/users/{id}       : removes a user. Req: admin or owner (admin cannot delete themselves here)
 *
 * --- USER ENDPOINTS (self-management) ---
 * - POST   /api/v1/users/register   : public self-registration (uses: UserRegisterDTO + optional MultipartFile)
 * - GET    /api/v1/users/me         : retrieves the authenticated user's profile (uses: UserFullResponseDTO)
 * - PUT    /api/v1/users/me         : updates basic data of own profile (Uses: UserUpdateDTO - sensitive fields filtered)
 * - PUT    /api/v1/users/me/password: password change (uses: PasswordUpdateDTO). validates match and old password
 * - PUT    /api/v1/users/me/image   : uploads or updates own profile picture (MultipartFile)
 * - DELETE /api/v1/users/me/image   : deletes own profile picture and the physical resource
 * - DELETE /api/v1/users/me         : account deletion by the user. Req: must not be an admin
 *
 * --- TECHNICAL NOTES ---
 * - All responses including images return an 'imageId'. Binary resources are retrieved at: GET /api/v1/images/{imageId}/media
 * - 'UserMapper' is used to convert entities to response DTOs
 * - Integrated with 'NotificationService' to log critical system actions
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
    @Autowired
    private NotificationService notificationService;
    // endregion



    // region =========== GetMapping =================
    // region 1. getUsers
    // use: retrieve paginated list of users as UserBasicResponseDTO
    // req: admin
    @GetMapping
    public ResponseEntity<Page<UserBasicResponseDTO>> getUsers(@PageableDefault(size = 10) Pageable pageable) {
        if (userService.isLoggedUserNotAdmin()) {   // filter out users
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(users.map(userMapper::toBasicDTO));
    }
    // endregion

    // region 2. getUser
    // use: retrieve full details of specific user as UserFullResponseDTO
    // req: admin or account owner
    @GetMapping ("/{id}")
    public ResponseEntity<UserFullResponseDTO> getUser (@PathVariable Long id) {
        User loggedUser = userService.getLoggedUser();
        User targetUser = userService.findById(id);

        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        // AO1 vuln prevention: check if admin or owner of the account (the user itself)
        boolean isAdmin = loggedUser != null && loggedUser.getRoles().contains("ADMIN");
        boolean isOwner = loggedUser != null && loggedUser.getId().equals(id);

        if (!isAdmin && !isOwner) {   // 403 if check fails
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(userMapper.toFullDTO(targetUser));
    }
    // endregion

    // region 3. getMyProfile
    // use: retrieve full profile details of currently authenticated user as UserFullResponseDTO
    @GetMapping("/me")
    public ResponseEntity<UserFullResponseDTO> getMyProfile() {
        User user = userService.getLoggedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(userMapper.toFullDTO(user));
    }
    // endregion

    // 4. stats
    // retrieve global system statistics as UserStatsDTO
    // req: admin
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
    // use: update authenticated user's password using PasswordUpdateDTO with old password validation
    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword (@Valid @RequestBody PasswordUpdateDTO passwords) {
        User user = userService.getLoggedUser();
        if (user == null) return ResponseEntity.status (HttpStatus.UNAUTHORIZED).build();

        if (!passwords.newPassword().equals(passwords.confirmPassword())) {
            throw new IllegalArgumentException ("Passwords do not match.");
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
    // use: update or upload a profile image for a specific user via MultipartFile
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
    // use: update basic profile fields (name, lastName, and phones) for the authenticated user using UserUpdateDTO
    @PutMapping("/me")
    public ResponseEntity<UserFullResponseDTO> updateMyProfile(@Valid @RequestBody UserUpdateDTO updateData) {
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
    // use: update specific user via UserUpdateDTO
    // admin: can edit all fields (roles, status, email)
    // owner: restricted to basic info.
    @PutMapping("/{id}")
    public ResponseEntity<UserFullResponseDTO> updateUser (
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateData) {

        User loggedUser = userService.getLoggedUser();
        User targetUser = userService.findById(id);

        if (targetUser == null) {   // case: error retrieving user
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = loggedUser != null && loggedUser.getRoles().contains("ADMIN");
        boolean isOwner = loggedUser != null && loggedUser.getId().equals(id);

        if (!isAdmin && !isOwner) {   // case: neither admin or owner of the account
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // a01 vuln fix
        if (isAdmin) {   // all fields
            if (updateData.name() != null) targetUser.setName(updateData.name());
            if (updateData.lastName() != null) targetUser.setLastName(updateData.lastName());
            if (updateData.email() != null) targetUser.setEmail(updateData.email());
            if (updateData.mainPhone() != null) targetUser.setMainPhone(updateData.mainPhone());
            if (updateData.secondaryPhone() != null) targetUser.setSecondaryPhone(updateData.secondaryPhone());
            if (updateData.enabled() != null) targetUser.setEnabled(updateData.enabled());
            if (updateData.moneySpent() != null) targetUser.setMoneySpent(updateData.moneySpent());
            if (updateData.roles() != null && !updateData.roles().isEmpty()) {
                targetUser.setRoles(updateData.roles());
            }
        } else {   // only basic fields
            if (updateData.name() != null) targetUser.setName(updateData.name());
            if (updateData.lastName() != null) targetUser.setLastName(updateData.lastName());
            if (updateData.mainPhone() != null) targetUser.setMainPhone(updateData.mainPhone());
            if (updateData.secondaryPhone() != null) targetUser.setSecondaryPhone(updateData.secondaryPhone());
        }

        userService.save(targetUser);
        notificationService.notify("Profile updated: " + targetUser.getEmail(), "fas fa-user-edit", "bg-info");

        return ResponseEntity.ok(userMapper.toFullDTO(targetUser));
    }
    // endregion

    // region 5. updateMyImage
    // upload or replace the profile picture for the currently authenticated user using a MultipartFile
    @PutMapping ("/me/image")
    public ResponseEntity<Void> updateMyImage(@RequestParam("image") MultipartFile imageFile) throws IOException {
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
    // manually create a new user with specific roles, status, and a generated default avatar via UserRequestDTO; restricted to Admin
    @PostMapping({"", "/"})
    public ResponseEntity<UserFullResponseDTO> createUser(@Valid @RequestBody UserRequestDTO req) {
        if (userService.isLoggedUserNotAdmin()) {   // filter out users
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = new User(req.name(), req.lastName(), req.email(),
                passwordEncoder.encode(req.password()), req.mainPhone(), req.secondaryPhone());

        user.setRoles(req.roles());
        if (req.enabled() != null) user.setEnabled(req.enabled());
        if (req.moneySpent() != null) user.setMoneySpent(req.moneySpent());

        // Generar avatar por defecto (el admin podrá subir imagen luego por el PUT de imagen)
        byte[] avatar = userService.generateDefaultAvatar("Usuario", user.getName(), new Color(13, 110, 253));
        user.setProfilePicture(new Image(avatar));

        userService.save(user);
        notificationService.notify ("Admin ha creado al usuario: " + user.getName(), "fas fa-user-plus", "bg-success");
        return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId())).body(userMapper.toFullDTO(user));
    }
    // endregion


    // region 2. registerUser
    // use: public self-registration via UserRegisterDTO and optional
    @PostMapping (value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(
            @Valid
            @RequestPart("userData") UserRegisterDTO req,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        // terms and conditions check
        if (!req.termsAccepted()) {
            return ResponseEntity.badRequest().body("Debes aceptar los términos y condiciones.");
        }

        // email and phones validation
        if (userService.emailExists(req.email())) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }
        if (userService.phoneExists(req.mainPhone())) {
            return ResponseEntity.badRequest().body("El teléfono principal ya está en uso.");
        }
        if (req.secondaryPhone() != null && !req.secondaryPhone().trim().isEmpty()) {
            if (userService.phoneExists(req.secondaryPhone())) {
                return ResponseEntity.badRequest().body("El teléfono secundario ya está en uso.");
            }
        }

        // creation of user
        User user = new User(
                req.name(),
                req.lastName(),
                req.email(),
                passwordEncoder.encode(req.password()),
                req.mainPhone(),
                req.secondaryPhone()
        );

        // role setting and enabling
        user.setRoles(java.util.Arrays.asList("USER"));
        user.setEnabled(true);

        // check whether pfp was attached or the default one needs to be generated
        if (imageFile != null && !imageFile.isEmpty()) {   // case: pfp attached
            user.setProfilePicture(new Image(imageFile.getBytes()));
        } else {   // case: default generation
            byte[] avatar = userService.generateDefaultAvatar("Usuario", user.getName(), new java.awt.Color(13, 110, 253));
            user.setProfilePicture(new Image(avatar));
        }

        // saving
        userService.save(user);
        notificationService.notify ("Nuevo registro: " + user.getName(), "fas fa-user-plus", "bg-success");
        return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId())).body(userMapper.toFullDTO(user));
    }
    // endregion
    // endregion



    // region =========== DeleteMapping =================
    // region 1. deleteUser
    // use: remove user from the system
    // req: admin (excluding self-deletion) or owner
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User loggedUser = userService.getLoggedUser();
        User targetUser = userService.findById(id);

        if (targetUser == null) return ResponseEntity.notFound().build();

        boolean isAdmin = loggedUser != null && loggedUser.getRoles().contains("ADMIN");
        boolean isOwner = loggedUser != null && loggedUser.getId().equals(id);

        if (!isAdmin && !isOwner) {   // case: unauthorized deletion request (neither admin nor account owner)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (isAdmin && isOwner) {   // fix and case: admin cant delete himself
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String emailDeleted = targetUser.getEmail();
        userService.delete (targetUser);   // object isntead of id (service doesnt have delete by id implemented)
        notificationService.notify("User deleted: " + emailDeleted, "fas fa-user-minus", "bg-warning");

        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 2. deleteMyAccount
    // allow non-admin user to delete their own account from the system
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        User loggedUser = userService.getLoggedUser();
        if (loggedUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (userService.isAdmin(loggedUser)) {   // prevent admin self-deletion
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.delete(loggedUser);
        notificationService.notify ("El usuario " + loggedUser.getName() + " ha eliminado su propia cuenta", "fas fa-user-minus", "bg-warning");
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 3. deleteMyImage
    // use: remove the profile picture from the authenticated user's account and delete the physical image entity
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
    // use: quickly enable or disable a user account
    // req: admin (prevents admin from disabling themselves)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long id) {
        User loggedUser = userService.getLoggedUser();

        // case: non-admin
        if (loggedUser == null || !loggedUser.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User targetUser = userService.findById(id);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        // prevent admin from disabling themselves (shouldnt be a possibility)
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