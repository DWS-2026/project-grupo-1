package es.codeurjc.daw.library.controller.rest;






// region =========== imports =================
import es.codeurjc.daw.library.dto.UserMapper;
import es.codeurjc.daw.library.dto.UserRequestDTO;
import es.codeurjc.daw.library.dto.UserResponseDTO;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
            Resource file = new ByteArrayResource(user.getProfilePicture());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/png") // Adjust if needed
                    .body(file);
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
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO request) {
        User user = new User(
                request.name(), request.lastName(), request.email(),
                passwordEncoder.encode(request.password()), // Ciframos la pass
                request.mainPhone(), null
        );
        user.setRoles(request.roles());

        userService.save(user);

        // Generamos la URL del nuevo recurso para la cabecera Location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();

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
    public ResponseEntity<Object> uploadImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) {
        User user = userService.findById(id);
        if (user != null) {
            try {
                if (imageFile != null && !imageFile.isEmpty()) {
                    // convert multipart to byte[] and store it
                    user.setProfilePicture(imageFile.getBytes());
                    userService.save(user);
                    return ResponseEntity.noContent().build(); // 204 no content
                } else {
                    return ResponseEntity.badRequest().build(); // 400 bad request (if empty)
                }
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build(); // 500 (if conversion fails)
            }
        }
        return ResponseEntity.notFound().build(); // 404 (if user doesnt exist)
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