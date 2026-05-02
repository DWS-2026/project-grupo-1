package es.apexexpeditions.library.controller.rest;




// region =========== imports =================
import es.apexexpeditions.library.security.jwt.AuthResponse;
import es.apexexpeditions.library.security.jwt.LoginRequest;
import es.apexexpeditions.library.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// endregion




@RestController
@RequestMapping ("/api/v1/auth")
public class AuthRestController {
    // region =========== autowired =================
    @Autowired
    private UserLoginService userLoginService;
    // endregion


    // region =========== PostMapping =================
    // 1 . /login
    // patched for a03/a05 vulns by using valid
    @PostMapping ("/login")
    public ResponseEntity<AuthResponse> login(
            HttpServletResponse response,
            @Valid @RequestBody LoginRequest loginRequest) {   // intercept massive payloads
        return userLoginService.login(response, loginRequest);
    }
    // endregion

    // 2. /refresh
    @PostMapping ("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletResponse response,
            @CookieValue(name = "RefreshToken", required = false) String refreshToken) {
        return userLoginService.refresh(response, refreshToken);
    }
    // endregion

    // 3. /logout
    @PostMapping ("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        return ResponseEntity.ok(userLoginService.logout(response));
    }
    // endregion
}