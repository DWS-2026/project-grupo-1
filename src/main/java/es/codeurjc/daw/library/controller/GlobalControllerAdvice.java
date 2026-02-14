package es.codeurjc.daw.library.controller;




import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;




/**
 * handles common model attributes for all controllers
 * ensures availability of essential data (CSRF tokens, user session info) to every view in app
 */
@ControllerAdvice
public class GlobalControllerAdvice { // controller to manage csrf + session

    @Autowired
    private UserRepository userRepository;

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
}