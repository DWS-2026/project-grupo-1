package es.apexexpeditions.library.controller.rest;






// region =========== imports =================
import es.apexexpeditions.library.dto.user.UserMapper;
import es.apexexpeditions.library.dto.user.UserRequestDTO;
import es.apexexpeditions.library.dto.user.UserResponseDTO;
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.ImageService;
import es.apexexpeditions.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    // region 1. "/api/v1/users"
    // retrieve all users
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable).map(userMapper::toDTO));
    }

    // region 2. "/{id}/image"
    // download a pfp
    @GetMapping("/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null && user.isHasProfilePicture()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/png") // Adjust if needed
                    .body(user.getProfilePicture().getImageFile());
        }
        return ResponseEntity.notFound().build();
    }
    // endregion
    // endregion


    // region 2. "/{id}"
    // retrieve user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(userMapper.toDTO(user));
        }
        return ResponseEntity.notFound().build();
    }
    // endregion
    // endregion




    // region =========== PostMapping =================
    // region 1. createUser
    // combines json and file to allow creation of user with pfp (optional)
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestPart("user") UserRequestDTO request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        User user = new User(request.name(), request.lastName(), request.email(),
                passwordEncoder.encode(request.password()), request.mainPhone(), null);
        user.setRoles(request.roles());

        if (imageFile != null && !imageFile.isEmpty()) {
            user.setProfilePicture(imageService.createImage(imageFile));   // store image
        } else {   // generate avatar and store in image entity
            byte[] avatar = userService.generateDefaultAvatar("Usuario", user.getName(), new Color(13, 110, 253));
            user.setProfilePicture(new Image (avatar));
        }

        userService.save(user);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(userMapper.toDTO(user));
    }
    // endregion
    // endregion




    // region =========== PutMapping =================
    // region 1. updateUser
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO updatedUserDTO) {
        User user = userService.findById(id);
        if (user != null) {
            // Update basic fields
            user.setName(updatedUserDTO.name());
            user.setLastName(updatedUserDTO.lastName());
            user.setEmail(updatedUserDTO.email());
            if (updatedUserDTO.password() != null && !updatedUserDTO.password().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUserDTO.password()));
            }
            userService.save(user);
            return ResponseEntity.ok(userMapper.toDTO(user));
        }
        return ResponseEntity.notFound().build();
    }
    // endregion

    // region 2. uploadImage
    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) throws IOException {
        User user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();

        if (user.getProfilePicture() != null) {
            // Reemplazamos los bytes en la imagen existente
            imageService.replaceImageFile(user.getProfilePicture().getId(), imageFile);
        } else {
            // Si no tenía, creamos una nueva
            user.setProfilePicture(imageService.createImage(imageFile));
            userService.save(user);
        }
        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion




    // region =========== DeleteMapping =================
    // region 1. "/{id}"
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            userService.delete(user);
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.notFound().build();
    }
    // endregion
    // endregion
}