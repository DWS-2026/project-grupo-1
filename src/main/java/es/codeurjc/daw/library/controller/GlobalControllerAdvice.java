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




@ControllerAdvice
public class GlobalControllerAdvice { // controller to manage csrf + session

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addCommonAttributes (HttpServletRequest request, Model model, Principal principal) {
        // csrf
        CsrfToken token = (CsrfToken) request.getAttribute ("_csrf");
        if (token != null) {
            model.addAttribute ("_csrf", token);
        }

        // user info
        if (principal != null) {
            User user = userRepository.findByEmail (principal.getName());
            if (user != null) {
                model.addAttribute ("currentUser", user);
                model.addAttribute ("logged", true);
            }
        } else {
            model.addAttribute ("logged", false);
        }
    }
}