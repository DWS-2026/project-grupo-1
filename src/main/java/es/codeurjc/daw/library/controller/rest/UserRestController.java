package es.codeurjc.daw.library.controller.rest;






// region =========== imports =================
import es.codeurjc.daw.library.dto.UserMapper;
import es.codeurjc.daw.library.dto.UserResponseDTO;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// endregion






@RestController
public class UserRestController {
    // region =========== autowired =================
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper; // Inyectas el traductor
    // endregion




    // region =========== GetMapping =================
    // region 1. "/api/v1/users"
    @GetMapping("/api/v1/users")
    public Page<UserResponseDTO> getUsers(Pageable pageable) {
        // Usas el mapper para transformar la página
        return userService.findAll(pageable).map(userMapper::toDTO);
    }
    // endregion
    // endregion
}