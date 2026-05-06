package es.apexexpeditions.library.controller.rest;






// region =========== imports =================
import es.apexexpeditions.library.dto.user.UserMapper;
import es.apexexpeditions.library.dto.user.UserRegisterDTO;
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.security.jwt.AuthResponse;
import es.apexexpeditions.library.security.jwt.LoginRequest;
import es.apexexpeditions.library.security.jwt.UserLoginService;
import es.apexexpeditions.library.service.UserService;
import es.apexexpeditions.library.service.NotificationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.net.URI;
// endregion






/**
 * API REST v1: authentication and access control
 * * --- SECURITY AND ACCESS STRUCTURE ---
 * - PUBLIC: entry points (login, register) and session management (refresh, logout)
 * * --- AUTHENTICATION ENDPOINTS ---
 * - POST   /api/v1/auth/register    : new user self-registration
 * - POST   /api/v1/auth/login       : user authentication and token issuance
 * - POST   /api/v1/auth/refresh     : access token renewal
 * - POST   /api/v1/auth/logout      : session termination
 */
@RestController
@RequestMapping ("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints públicos para el registro de usuarios, inicio de sesión y gestión de tokens de seguridad")
public class AuthRestController {
    // region =========== autowired =================
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NotificationService notificationService;
    // endregion




    // region =========== PostMapping =================
    // region 1. /register
    // use: self-register a new user in the system with optional profile picture
    // req: none
    @Operation(summary = "Registrar un nuevo usuario", 
               description = "Permite crear una nueva cuenta de usuario en el sistema. Se puede adjuntar de forma opcional una imagen de perfil.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito",
            content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = es.apexexpeditions.library.dto.user.UserFullResponseDTO.class)) }), // <- Apunta al DTO de respuesta real
        @ApiResponse(responseCode = "400", description = "Datos de registro inválidos (formato de correo incorrecto, contraseñas débiles o campos obligatorios vacíos)", 
            content = @Content)
    })
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

    // region 2 . /login
    // use: authenticate user credentials and receive jwt access and refresh tokens
    // req: none previously (but knowing user credentials to proceed)
    @Operation(summary = "Iniciar sesión", 
               description = "Autentica las credenciales del usuario (email y contraseña). Si son válidas, establece las cookies seguras y devuelve los tokens JWT de acceso y refresco.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
            content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = AuthResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o payload de inicio de sesión corrupto", 
            content = @Content)
    })
    @PostMapping ("/login")
    public ResponseEntity<AuthResponse> login(
            HttpServletResponse response,
            @Valid @RequestBody LoginRequest loginRequest) {   // intercept massive payloads
        return userLoginService.login(response, loginRequest);
    }
    // endregion

    // region 3. /refresh
    // use: renew an expired access token using the RefreshToken stored in a secure cookie
    // req: none (but having a valid RefreshToken cookie)
    @Operation(summary = "Refrescar token de acceso", 
               description = "Renueva un token de acceso JWT expirado utilizando la Cookie segura 'RefreshToken' que tiene almacenada el navegador del usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token renovado con éxito",
            content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = AuthResponse.class)) }),
        @ApiResponse(responseCode = "401", description = "Token de refresco inválido o expirado", 
            content = @Content)
    })
    
    @PostMapping ("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletResponse response,
            @CookieValue(name = "RefreshToken", required = false) String refreshToken) {
        return userLoginService.refresh(response, refreshToken);
    }
    // endregion


    // region 4. /logout
    // use: invalidate the current session and clear authentication cookies
    // req: none
    @Operation(summary = "Cerrar sesión", 
               description = "Invalida la sesión actual del usuario y limpia las cookies HTTP-only de autenticación almacenadas en el navegador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente",
            content = { @Content(mediaType = "text/plain") })
    })
    @PostMapping ("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        return ResponseEntity.ok(userLoginService.logout(response));
    }
    // endregion
    // endregion
}