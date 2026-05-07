package es.apexexpeditions.library.controller;

// region =========== imports =================
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.repository.UserRepository;
import es.apexexpeditions.library.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
// endregion
import java.util.Map;

/**
 * handles common model attributes for all controllers
 * ensures availability of essential data (CSRF tokens, user session info) to
 * every view in app
 */
@ControllerAdvice
public class GlobalControllerAdvice { // controller to manage csrf + session
    // region =========== Autowired =================
    @Autowired
    private UserRepository userRepository;

    @Autowired // to manage notifications
    private NotificationService notificationService;
    // endregion

    // region =========== ModelAttribute =================
    // region 1. "addCommonAttributes"
    /**
     * adds common attributes to model before any controller method is executed
     * runs for every request handled by a controller
     *
     * @param request   http request used to retrieve CSRF token
     * @param model     spring mcv model where attributes are stored for the view
     * @param principal currently authenticated user (if any)
     */
    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest request, Model model, Principal principal) {
        // csrf token management:
        // retrieve token from request attributes and add to model
        // forms required to include "_csrf" input
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("_csrf", token);
        }

        // user session management
        // check if user logged in via principal object
        if (principal != null) {
            // fetch full user details from db using email (username)
            User user = userRepository.findByEmail(principal.getName());
            if (user != null) {
                // expose user object and boolean flag to views
                model.addAttribute("currentUser", user);
                model.addAttribute("logged", true);

                // check if user admin role
                boolean isAdmin = user.getRoles().contains("ADMIN");
                model.addAttribute("isAdmin", isAdmin);

            }
        } else {
            // if no user authenticated set flag to false
            model.addAttribute("logged", false);
        }
    }
    // endregion

    // region 2. "addNotifications"
    @ModelAttribute
    public void addNotifications(Model model) {
        // retrieve latest 10 notifications
        model.addAttribute("notifications", notificationService.getRecent10());

        // count unread notifications to update red background counter
        long unreadCount = notificationService.getUnreadCount();
        model.addAttribute("unreadCount", unreadCount);

        // flag
        model.addAttribute("hasNotifications", unreadCount > 0);
    }
    // endregion
    // endregion

    // region =========== ExceptionHandler =================
    // region 1. handleNotFound
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri != null && uri.startsWith("/api/")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "error", "Resource not found",
                            "status", 404,
                            "path", uri));
        }

        return "error/404";
    }
    // endregion

    // region =========== ExceptionHandler =================
    // region 2. handleTypeMismatch (false positive sql injection related)
    // capture errores when sending letters or symbols in boolean or numeral
    // parameters (avoids leaking stack trace in 500)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // 1. ¿Es una petición de la API?
        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", 400);
            body.put("error", "Bad Request");
            body.put("message", "Formato de dato inválido para el parámetro: " + ex.getName());
            body.put("path", request.getRequestURI());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        // 2. Si no es API, seguimos con tu lógica de redirección Web
        redirectAttributes.addFlashAttribute("errorMessage", "Data sent in an invalid format.");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    // region 3. handleAllExceptions (stack leak protection)
    // capture any unmanaged internal error and redirect in order to avoid leaking
    // info
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/")) {
            if (ex instanceof ResponseStatusException) {
                ResponseStatusException rse = (ResponseStatusException) ex;
                return ResponseEntity
                        .status(rse.getStatusCode())
                        .body(Map.of(
                                "error", rse.getReason() != null ? rse.getReason() : "Error",
                                "status", rse.getStatusCode().value()));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", ex.getMessage() != null ? ex.getMessage() : "Internal server error",
                            "status", 500));
        }

        return "error/404";
    }

    // endregion
    // endregion
}