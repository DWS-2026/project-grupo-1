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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Usuarios", description = "Gestión de perfiles, permisos, edición y estadísticas del sistema")
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
    @Operation(summary = "Listado paginado general", description = "Muestra todos los usuarios (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado correcto"),
        @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
    })
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
    @Operation(summary = "Detalle de perfil específico", description = "Muestra información de un usuario concreto (Requiere ser el dueño de la cuenta o ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil recuperado", content = @Content(schema = @Schema(implementation = UserFullResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Ataque detectado (AO1 previsor)", content = @Content),
        @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content)
    })
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
    @Operation(summary = "Obtener mi propio perfil", description = "Saca los datos asociados al usuario actual según su token de sesión.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos correctos", content = @Content(schema = @Schema(implementation = UserFullResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Debes iniciar sesión", content = @Content)
    })
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
    @Operation(summary = "Estadísticas de usuarios", description = "Datos genéricos sobre base de usuarios del sistema (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Métricas entregadas", content = @Content(schema = @Schema(implementation = UserStatsDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
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
    @Operation(summary = "Actualizar contraseña", description = "Cambio de la clave personal validando primero la anterior.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Contraseña cambiada"),
        @ApiResponse(responseCode = "400", description = "Claves mal formadas o no coinciden", content = @Content),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Contraseña antigua errónea", content = @Content)
    })
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
    @Operation(summary = "Actualizar foto de perfil", description = "Sustituye la foto de perfil de cualquier persona. (Normalmente invocado por ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Foto de perfil modificada"),
        @ApiResponse(responseCode = "404", description = "Usuario destino no localizado", content = @Content)
    })
    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) throws IOException {
        User loggedUser = userService.getLoggedUser();
        User user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();

        boolean isAdmin = loggedUser != null && userService.isAdmin(loggedUser);
        boolean isOwner = loggedUser != null && loggedUser.getId().equals(id);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

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
    @Operation(summary = "Editar mi perfil", description = "Actualiza los campos básicos del usuario autenticado de forma segura (sin roles ni dinero).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cambios guardados", content = @Content(schema = @Schema(implementation = UserFullResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Falta sesión", content = @Content)
    })
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
    @Operation(summary = "Edición general de cuenta", description = "Si eres dueño edita campos base. Si eres ADMIN tienes permisos de edición de cuenta total (Baneo, Modificar saldo, etc).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Modificado"),
        @ApiResponse(responseCode = "403", description = "Intento ilegal de modificación a cuenta ajena", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no detectado", content = @Content)
    })
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
    @Operation(summary = "Cambiar mi foto", description = "Petición multipart para cambiar la foto de perfil de mi cuenta personal.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Foto asignada"),
        @ApiResponse(responseCode = "401", description = "Error de sesión", content = @Content)
    })
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
    @Operation(summary = "Creación de usuario por administrador", description = "Manual override: Fuerza la creación de cuenta, roles y dinero gastado inicial. (Requiere ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario Creado", content = @Content(schema = @Schema(implementation = UserFullResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Privilegios insuficientes", content = @Content)
    })
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
    // endregion



    // region =========== DeleteMapping =================
    // region 1. deleteUser
    // use: remove user from the system
    // req: admin (excluding self-deletion) or owner
    @Operation(summary = "Eliminar cuenta", description = "Borra físicamente a un usuario del sistema (Requiere ADMIN). NOTA: Los admin no pueden autoeliminarse por este endpoint.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cuenta borrada"),
        @ApiResponse(responseCode = "403", description = "Faltan permisos o prevención de auto-borrado ADMIN", content = @Content),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content)
    })
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
    @Operation(summary = "Eliminación de cuenta (por usuario)", description = "Permite a un usuario normal eliminar su propia cuenta de la base de datos.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Baja finalizada"),
        @ApiResponse(responseCode = "401", description = "No has iniciado sesión", content = @Content),
        @ApiResponse(responseCode = "403", description = "Los ADMINS deben darse de baja por base de datos directa para seguridad", content = @Content)
    })
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
    @Operation(summary = "Borrar mi foto de perfil", description = "Quita la foto de perfil y elimina su blob en BDD, regresando al avatar por defecto visualmente.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Imagen destruida"),
        @ApiResponse(responseCode = "401", description = "Sin sesión", content = @Content)
    })
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
    @Operation(summary = "Suspender/Reactivar usuario", description = "Invierte el campo enabled (banea o desbanea del inicio de sesión) (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estado volcado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Intento no administrador o prevención de suicidio digital", content = @Content),
        @ApiResponse(responseCode = "404", description = "Objetivo a banear no hallado", content = @Content)
    })
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
