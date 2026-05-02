package es.apexexpeditions.library.controller;






// region =========== imports =================
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.repository.UserRepository;
import es.apexexpeditions.library.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
// endregion




/**
 * handles common model attributes for all controllers
 * ensures availability of essential data (CSRF tokens, user session info) to every view in app
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
    public void addCommonAttributes (HttpServletRequest request, Model model, Principal principal) {
        // csrf token management:
        // retrieve token from request attributes and add to model
        // forms required to include "_csrf" input
        CsrfToken token = (CsrfToken) request.getAttribute ("_csrf");
        if (token != null) {
            model.addAttribute ("_csrf", token);
        }

        // user session management
        // check if user logged in via principal object
        if (principal != null) {
            // fetch full user details from db using email (username)
            User user = userRepository.findByEmail (principal.getName());
            if (user != null) {
                // expose user object and boolean flag to views
                model.addAttribute ("currentUser", user);
                model.addAttribute ("logged", true);
                
                // check if user admin role
                boolean isAdmin = user.getRoles().contains("ADMIN");
                model.addAttribute("isAdmin", isAdmin);
                
            }
        } else {
            // if no user authenticated set flag to false
            model.addAttribute ("logged", false);
        }
    }
    // endregion



    // region 2. "addNotifications"
    @ModelAttribute
    public void addNotifications (Model model) {
        // retrieve latest 10 notifications
        model.addAttribute ("notifications", notificationService.getRecent10());

        // count unread notifications to update red background counter
        long unreadCount = notificationService.getUnreadCount();
        model.addAttribute("unreadCount", unreadCount);

        // flag
        model.addAttribute ("hasNotifications", unreadCount > 0);
    }
    // endregion
    // endregion




    // region =========== ExceptionHandler =================
    // region 1. handleTypeMismatch (false positive sql injection related)
    // capture errores when sending letters or symbols in boolean or numeral parameters (avoids leaking stack trace in 500)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch (MethodArgumentTypeMismatchException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute ("errorMessage", "Data sent in an invalid format.");
        // redirect user to previous page or home (if no previous page)
        String referer = request.getHeader ("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
    // endregion

    // region 2. handleAllExceptions (stack leak protection)
    // capture any unmanaged internal error and redirect in order to avoid leaking info
    @ExceptionHandler (Exception.class)
    public String handleAllExceptions (Exception ex) {
        return "redirect:/admin/404";
    }
    // endregion
    // endregion
}