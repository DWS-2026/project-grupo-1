package es.codeurjc.daw.library.controller;



import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.TourService;
import es.codeurjc.daw.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;







@Controller
public class WebController {
    @Autowired
    private UserService userService;
    @Autowired
    private TourService tourService;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @GetMapping ("/")
    public String index (Model model) {
        model.addAttribute ("home", true);
        return "user/index";
    }



    @GetMapping ("/packages")
    public String packages (Model model) {
        model.addAttribute ("tours", tourService.getAllTours());
        return "user/packages";
    }



    @GetMapping ("/guides")
    public String guides (Model model) {
        return "user/guides";
    }



    @GetMapping ("/services")
    public String services (Model model) {
        model.addAttribute ("services", true);
        return "user/services";
    }



    @GetMapping ("/about")
    public String about (Model model) {
        model.addAttribute ("about", true);
        return "user/about";
    }



    @GetMapping ("/contact")
    public String contact (Model model) {
        model.addAttribute ("contact", true);
        return "user/contact";
    }



    @GetMapping ("/cart")
    public String cart (Model model) {
        model.addAttribute ("cart", true);
        return "user/cart";
    }



    @GetMapping ("/register")
    public String register() {
        return "user/register";
    }



    @PostMapping ("/register")
    public String registerUser (@RequestParam String name,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam String mainPhone,
                               @RequestParam String password,
                               Model model) {

        // create user instance
        User newUser = new User();
        newUser.setName (name);
        newUser.setLastName (lastName);
        newUser.setEmail (email);
        newUser.setMainPhone (mainPhone);
        newUser.setEnabled (true);

        newUser.setRoles (Arrays.asList("USER")); // assign user role
        newUser.setPassword (passwordEncoder.encode(password)); // encrypt password
        byte[] avatar = userService.generateDefaultAvatar ("Usuario", name, new Color(13, 110, 253));
        newUser.setProfilePicture (avatar);  // default user pfp

        userService.save (newUser); // save user
        return "redirect:/login"; // go login
    }



    @GetMapping ("/login")
    public String login (@RequestParam(required = false) String error, Model model) {
        model.addAttribute ("error", error != null);
        return "user/login";
    }



    @GetMapping ("/profile")
    public String profile (Model model, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            User user = userService.findByEmail (principal.getName());
            model.addAttribute ("user", user);

            // check if user admin, and set flag accordingly
            boolean isAdmin = user.getRoles().contains("ADMIN") || request.isUserInRole("ADMIN");
            model.addAttribute ("isAdmin", isAdmin);
        }
        return "user/profile";
    }



    @GetMapping ("/tour-details/{id}")
    public String showDetails (@PathVariable Long id, Model model) {
    model.addAttribute ("tour", tourService.findById(id));
        return "user/tour-details";
    }



    @GetMapping ("/checkout")
    public String checkout() {
        return "user/checkout";
    }



    @GetMapping ("/invoice")
    public String invoice() {
        return "/user/invoice";
    }



    @GetMapping ("/add-review")
    public String add_review() {
        return "/user/add-review";
    }



    @GetMapping ("/forgot-password")
    public String forgot_password() {
        return "/user/forgot-password";
    }



    @GetMapping ("/admin-login")
    public String adminLogin (@RequestParam(required = false) String error, Model model) {
        model.addAttribute ("error", error != null);
        return "user/admin-login";
    }



    @PostMapping ("/profile/update")
    public String updateProfile (Principal principal,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String mainPhone,
            @RequestParam String secondaryPhone,
            @RequestParam (required = false) String newPassword,
            @RequestParam MultipartFile imageFile,
            @RequestParam String action) throws IOException {

        // get user logged in
        User user = userService.findByEmail (principal.getName());

        // process user deletion
        if ("delete".equals (action)) {
            userService.delete (user);
            return "redirect:/logout";
        }

        // update info
        user.setName (name);
        user.setLastName (lastName);
        user.setMainPhone (mainPhone);
        user.setSecondaryPhone (secondaryPhone);

        // process new image (if uploaded)
        if (!imageFile.isEmpty()) {
            byte[] bytes = imageFile.getBytes();
            user.setProfilePicture(imageFile.getBytes());
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


    // retrieve an image for a specific user
    @GetMapping("/user/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null && user.getProfilePicture() != null) {
            return ResponseEntity.ok()
                    .header (HttpHeaders.CONTENT_TYPE, "image/png")
                    .body (user.getProfilePicture());
        }
        return ResponseEntity.notFound().build();
    }
}
