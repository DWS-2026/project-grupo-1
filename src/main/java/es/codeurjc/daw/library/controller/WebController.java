package es.codeurjc.daw.library.controller;



import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.service.GuideService;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ReviewService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.awt.*;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Controller
public class WebController {
    @Autowired
    private UserService userService;
    @Autowired
    private TourService tourService;
     @Autowired
    private ReviewService reviewService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GuideService guideService;



    @GetMapping ("/")
    public String index (Model model) {
        model.addAttribute ("home", true);
        return "user/index";
    }



    @GetMapping ("/packages")
    public String packages (Model model, @PageableDefault(size = 6) Pageable pageable) {
        Page<Tour> page = tourService.findByHiddenFalse(pageable);

        model.addAttribute("tours", page.getContent());

        model.addAttribute("hasPrev", page.hasPrevious());
        model.addAttribute("hasNext", page.hasNext());
        model.addAttribute("prev", page.getNumber() - 1);
        model.addAttribute("next", page.getNumber() + 1);
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("size", pageable.getPageSize());


        return "user/packages";
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
                                @RequestParam(required = false) String secondaryPhone,
                                @RequestParam(required = false) MultipartFile imageFile, // pfp
                                Model model) throws IOException {

        // check email in use
        if (userService.emailExists (email)) {
            model.addAttribute ("errorMessage", "El correo electrónico ya está registrado en otra cuenta.");
            return "user/register";
        }

        // check main phone in use
        if (userService.phoneExists (mainPhone)) {
            model.addAttribute ("errorMessage", "El teléfono principal ya está en uso.");
            return "user/register";
        }

        // check secondary phone (if sent) is used
        if (secondaryPhone != null && !secondaryPhone.trim().isEmpty()) {
            if (userService.phoneExists (secondaryPhone)) {
                model.addAttribute ("errorMessage", "El teléfono secundario ya está en uso por otra cuenta.");
                return "user/register";
            }
            // check main and secondary arent same
            if (mainPhone.equals (secondaryPhone)) {
                model.addAttribute ("errorMessage", "El teléfono principal y secundario no pueden ser el mismo.");
                return "user/register";
            }
        }

        // if no repetitions, create user
        User newUser = new User (name, lastName, email, passwordEncoder.encode (password), mainPhone, secondaryPhone);

        // LÓGICA DE LA FOTO DE PERFIL
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // 1. Si el usuario ha adjuntado una imagen, guardamos sus bytes
                newUser.setProfilePicture(imageFile.getBytes());
            } else {
                // 2. Si no adjunta imagen, generamos la predeterminada automáticamente
                byte[] avatar = userService.generateDefaultAvatar("Usuario", name, new Color(13, 110, 253));
                newUser.setProfilePicture(avatar);
            }
        } catch (IOException e) {
            // En caso de error leyendo el archivo, asignamos el avatar por defecto como salvavidas
            e.printStackTrace();
            byte[] avatar = userService.generateDefaultAvatar("Usuario", name, new Color(13, 110, 253));
            newUser.setProfilePicture(avatar);
        }

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
    public String showDetails(@PathVariable Long id,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {

        var tour = tourService.findById(id);
        Page<Review> reviewPage = reviewService.findPagedByTourIdAndHiddenFalse(id, page);

        model.addAttribute("tour", tour);
        model.addAttribute("reviews", reviewPage.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", reviewPage.hasNext());
        model.addAttribute("nextPage", page + 1);

        model.addAttribute("hasPrevious", reviewPage.hasPrevious());
        model.addAttribute("previousPage", page - 1);

        double averageRating = reviewService.getAverageRating(id);
        int totalReviews = reviewService.getTotalReviews(id);

        long count5 = reviewService.countByRating(id, 5);
        long count4 = reviewService.countByRating(id, 4);
        long count3 = reviewService.countByRating(id, 3);
        long count2 = reviewService.countByRating(id, 2);
        long count1 = reviewService.countByRating(id, 1);

        model.addAttribute("averageRating", String.format("%.1f", averageRating));
        model.addAttribute("totalReviews", totalReviews);

        model.addAttribute("count5", count5);
        model.addAttribute("count4", count4);
        model.addAttribute("count3", count3);
        model.addAttribute("count2", count2);
        model.addAttribute("count1", count1);

        model.addAttribute("percent5", totalReviews == 0 ? 0 : (count5 * 100 / totalReviews));
        model.addAttribute("percent4", totalReviews == 0 ? 0 : (count4 * 100 / totalReviews));
        model.addAttribute("percent3", totalReviews == 0 ? 0 : (count3 * 100 / totalReviews));
        model.addAttribute("percent2", totalReviews == 0 ? 0 : (count2 * 100 / totalReviews));
        model.addAttribute("percent1", totalReviews == 0 ? 0 : (count1 * 100 / totalReviews));

        model.addAttribute("avgStar1", averageRating >= 1);
        model.addAttribute("avgStar2", averageRating >= 2);
        model.addAttribute("avgStar3", averageRating >= 3);
        model.addAttribute("avgStar4", averageRating >= 4);
        model.addAttribute("avgStar5", averageRating >= 5);

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



    @GetMapping("/add-review/{id}")
    public String showAddReview(@PathVariable Long id, Model model) {

        var tour = tourService.findById(id);

        double averageRating = reviewService.getAverageRating(id);
        int totalReviews = reviewService.getTotalReviews(id);

        long count5 = reviewService.countByRating(id, 5);
        long count4 = reviewService.countByRating(id, 4);
        long count3 = reviewService.countByRating(id, 3);
        long count2 = reviewService.countByRating(id, 2);
        long count1 = reviewService.countByRating(id, 1);

        model.addAttribute("tour", tour);

        model.addAttribute("averageRating", String.format("%.1f", averageRating));
        model.addAttribute("totalReviews", totalReviews);

        model.addAttribute("count5", count5);
        model.addAttribute("count4", count4);
        model.addAttribute("count3", count3);
        model.addAttribute("count2", count2);
        model.addAttribute("count1", count1);

        model.addAttribute("percent5Width", (totalReviews == 0 ? 0 : (count5 * 100 / totalReviews)) + "%");
        model.addAttribute("percent4Width", (totalReviews == 0 ? 0 : (count4 * 100 / totalReviews)) + "%");
        model.addAttribute("percent3Width", (totalReviews == 0 ? 0 : (count3 * 100 / totalReviews)) + "%");
        model.addAttribute("percent2Width", (totalReviews == 0 ? 0 : (count2 * 100 / totalReviews)) + "%");
        model.addAttribute("percent1Width", (totalReviews == 0 ? 0 : (count1 * 100 / totalReviews)) + "%");

        model.addAttribute("avgStar1", averageRating >= 1);
        model.addAttribute("avgStar2", averageRating >= 2);
        model.addAttribute("avgStar3", averageRating >= 3);
        model.addAttribute("avgStar4", averageRating >= 4);
        model.addAttribute("avgStar5", averageRating >= 5);

        return "user/add-review";
    }

    @GetMapping("/review-user")
    public String myReviews(Principal principal,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/";
        }

        Page<Review> reviewPage = reviewService.findPagedByUserId(user.getId(), page);

        model.addAttribute("reviews", reviewPage.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", reviewPage.hasNext());
        model.addAttribute("nextPage", page + 1);

        model.addAttribute("hasPrevious", reviewPage.hasPrevious());
        model.addAttribute("previousPage", page - 1);

        return "user/review-user";
    }

    @GetMapping("/mis-reviews/{id}/edit-review")
    public String editReview(@PathVariable Long id, Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Review> optionalReview = reviewService.findById(id);

        if (optionalReview.isEmpty()) {
            return "redirect:/review-user";
        }

        Review review = optionalReview.get();

        model.addAttribute("review", review);
        model.addAttribute("tour", review.getTour());

        return "user/edit-review";
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

    @GetMapping("/guides")
    public String guides(Model model) {

        List<Guide> guides = guideService.findAll();

        if (guides.size() > 6) {
            guides = guides.subList(0, 6);
        }

        model.addAttribute("guides", guides);

        return "user/guides";
    }

}
