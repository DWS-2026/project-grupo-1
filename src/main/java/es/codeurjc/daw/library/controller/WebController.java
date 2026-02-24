package es.codeurjc.daw.library.controller;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.TourService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private TourService tourService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("home", true);
        return "user/index";
    }

    @GetMapping("/packages")
    public String packages(Model model) {
        model.addAttribute("tours", tourService.getAllTours());
        return "user/packages";
    }

    @GetMapping("/guides")
    public String guides(Model model) {
        return "user/guides";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", true);
        return "user/services";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("about", true);
        return "user/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("contact", true);
        return "user/contact";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("cart", true);
        return "user/cart";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error != null);
        return "user/login";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("user", user);

            // check if user admin, and set flag accordingly
            boolean isAdmin = user.getRoles().contains("ADMIN") || request.isUserInRole("ADMIN");
            model.addAttribute("isAdmin", isAdmin);
        }
        return "user/profile";
    }

    @GetMapping("/tour-details")
    public String tour_details(){return "user/tour-details";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "user/checkout";
    }

    @GetMapping("/invoice")
    public String invoice() {
        return "/user/invoice";
    }

    @GetMapping("/add-review")
    public String add_review() {
        return "/user/add-review";
    }

    @GetMapping("/forgot-password")
    public String forgot_password() {
        return "/user/forgot-password";
    }

    @GetMapping("/admin-login")
    public String adminLogin(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error != null);
        return "user/admin-login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Principal principal,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String mainPhone,
            @RequestParam String secondaryPhone,
            @RequestParam(required = false) String newPassword,
            @RequestParam MultipartFile imageFile,
            @RequestParam String action) throws IOException {

        // get user logged in
        User user = userService.findByEmail(principal.getName());

        // process user deletion
        if ("delete".equals(action)) {
            userService.delete(user);
            return "redirect:/logout";
        }

        // 3. Actualizar datos básicos
        user.setName(name);
        user.setLastName(lastName);
        user.setMainPhone(mainPhone);
        user.setSecondaryPhone(secondaryPhone);

        // process new image (if uploaded)
        if (!imageFile.isEmpty()) {
            byte[] bytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(bytes);
            user.setProfilePicture(base64Image);
        }

        // change password (if there is a new one)
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        // save changes
        userService.save(user);

        // return to profile page
        return "redirect:/profile";
    }
}
