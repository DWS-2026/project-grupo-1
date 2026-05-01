package es.apexexpeditions.library.controller;

// region =========== imports =================
import es.apexexpeditions.library.dto.user.UserRegisterDTO;
import es.apexexpeditions.library.model.Guide;
import es.apexexpeditions.library.model.Booking;
import es.apexexpeditions.library.model.Review;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.service.*;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.model.Image;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.ArrayList;
import java.awt.*;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
// endregion

/**
 * Controller to manage general web navigation and user actions.
 */
@Controller
public class WebController {
    // region =========== Autowired =================
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
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private BookingService bookingService;
    // endregion

    // region =========== ModelAttribute =================
    /**
     * Retrieves the current user's cart size to display in the view.
     *
     * @param principal The currently authenticated user.
     * @return The number of tours in the open booking, or 0 if none.
     */
    @ModelAttribute("cartSize")
    public int getCartSize(Principal principal) {
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            Optional<Booking> bookingOpt = bookingService.findOpenBookingByUser(user);
            if (bookingOpt.isPresent()) {
                return bookingOpt.get().getTours().size();
            }
        }
        return 0; // triggered if: user has no bookings or isnt logged in
    }
    // endregion

    // region =========== GetMapping =================
    // region 1. "/"
    /**
     * Handles the home page request.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("home", true);
        return "user/index";
    }
    // endregion

    // region 2. "/packages"
    /**
     * Displays available tours with pagination.
     */
    @GetMapping("/packages")
    public String packages(Model model, @PageableDefault(size = 6) Pageable pageable) {
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
    // endregion

    // region 3. "/services"
    /**
     * Displays the services page.
     */
    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", true);
        return "user/services";
    }
    // endregion

    // region 4. "/about"
    /**
     * Displays the about page.
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("about", true);
        return "user/about";
    }
    // endregion

    // region 5. "/contact"
    /**
     * Displays the contact page and handles potential WIP messages.
     */
    @GetMapping("/contact")
    public String contact(@RequestParam(required = false) String wip, Model model) {
        model.addAttribute("contact", true);
        if (wip != null) { // if user tries to send message, set flag to true (and then display popup)
            model.addAttribute("showContactWip", true);
        }

        return "user/contact";
    }
    // endregion

    // region 6. "/cart/"
    // region 6.1. "/cart/add/"
    /**
     * Adds a specific tour to the user's active booking/cart.
     */
    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, Principal principal, RedirectAttributes data) {
        if (principal == null) {
            return "redirect:/login"; // forces login if to do booking
        }

        User user = userService.findByEmail(principal.getName());
        Tour tour = tourService.findById(id);

        if (tour != null) {
            // if user has no booking existing, create it
            Booking booking = bookingService.findOpenBookingByUser(user)
                    .orElseGet(() -> new Booking(user));

            // avoid having 2 instances of same tour in 1 booking
            if (!booking.getTours().contains(tour)) {
                booking.getTours().add(tour);
                bookingService.save(booking); // save to db
                data.addFlashAttribute("showSuccess", true);
            } else {
                data.addFlashAttribute("showError", true);
                data.addFlashAttribute("tourName", tour.getName());
            }
        }
        return "redirect:/cart";
    }
    // endregion

    // region 6.2. "/cart/remove/"
    /**
     * Removes a tour from the user's active booking/cart.
     */
    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        Optional<Booking> bookingOpt = bookingService.findOpenBookingByUser(user);

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.getTours().removeIf(t -> t.getId().equals(id));

            // store to db
            bookingService.save(booking);
        }

        // redirect
        return "redirect:/cart?removed=true";
    }
    // endregion

    // region 6.3. "/cart"
    /**
     * Displays the user's shopping cart contents.
     */
    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        model.addAttribute("cart", true);
        // initialize default vars
        model.addAttribute("cartItems", new ArrayList<Tour>());
        model.addAttribute("total", "0.00");
        model.addAttribute("isEmpty", true);

        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            Optional<Booking> bookingOpt = bookingService.findOpenBookingByUser(user);

            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();
                List<Tour> tours = booking.getTours();

                if (!tours.isEmpty()) {
                    model.addAttribute("cartItems", tours);
                    model.addAttribute("total", String.format("%.2f", booking.getTotalPrice()));
                    model.addAttribute("isEmpty", false);
                }
            }
        }
        return "user/cart";
    }
    // endregion
    // endregion

    // region 7. "/register"
    /**
     * Displays the registration form.
     */
    @GetMapping("/register")
    public String register() {
        return "user/register";
    }
    // endregion

    // region 8. "/login"
    /**
     * Displays the user login form. Manages error or status flags.
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String inactive,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String changed,
            Model model) {

        // if url includes "?inactive", set inactive flag to true
        if (inactive != null) {
            model.addAttribute("isInactive", true);
        }

        // if URL includes "?changed", set flag to display related message
        if (changed != null) {
            model.addAttribute("changed", true);
        }

        return "user/login";
    }
    // endregion

    // region 9. "/profile"
    /**
     * Displays the profile page for the authenticated user.
     */
    @GetMapping("/profile")
    public String profile(Model model,
            Principal principal,
            HttpServletRequest request,
            @RequestParam(required = false) Boolean showLogout) {

        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("user", user);

            // if user doesnt exist in db, return
            if (user == null) {
                return "redirect:/logout";
            }

            // if a param showLogout is received, set flag
            if (Boolean.TRUE.equals(showLogout)) {
                model.addAttribute("openLogoutModal", true);
            }

            model.addAttribute("currentUser", user);

            // check if user admin, and set flag accordingly
            boolean isAdmin = user.getRoles().contains("ADMIN") || request.isUserInRole("ADMIN");
            model.addAttribute("isAdmin", isAdmin);
        }
        return "user/profile";
    }
    // endregion

    // region 10. "tour-details/"
    /**
     * Displays detailed information about a specific tour, including its paginated reviews.
     */
    @GetMapping("/tour-details/{id}")
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
    // endregion

    // region 11. "/checkout"
    /**
     * Renders the checkout page for completing a purchase.
     */
    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        User user = userService.findByEmail(principal.getName());
        Optional<Booking> bookingOpt = bookingService.findOpenBookingByUser(user);

        if (bookingOpt.isPresent() && !bookingOpt.get().getTours().isEmpty()) {
            Booking booking = bookingOpt.get();
            model.addAttribute("cartItems", booking.getTours());
            model.addAttribute("total", String.format("%.2f", booking.getTotalPrice()));
            return "user/checkout";
        }

        return "redirect:/cart"; // if no booking open, return to cart
    }
    // endregion

    // region 12. "/invoice"
    /**
     * Processes a completed booking and displays the generated invoice.
     */
    @GetMapping("/invoice")
    public String invoice(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        Optional<Booking> openBookingOpt = bookingService.findOpenBookingByUser(user);

        if (openBookingOpt.isPresent() && !openBookingOpt.get().getTours().isEmpty()) {
            Booking openBooking = openBookingOpt.get();
            // close reserve (empty cart)
            openBooking.setClose(true);
            bookingService.save(openBooking);
            user.addMoneySpent(openBooking.getTotalPrice());
            userService.save(user);
        }

        Optional<Booking> lastClosedOpt = bookingService.findLastClosedBookingByUser(user);

        if (lastClosedOpt.isPresent()) {
            Booking booking = lastClosedOpt.get();
            double total = booking.getTotalPrice();

            // pass data to build final invoice
            model.addAttribute("cartItems", booking.getTours());
            model.addAttribute("subtotal", String.format("%.2f", total * 0.79));
            model.addAttribute("tax", String.format("%.2f", total * 0.21));
            model.addAttribute("total", String.format("%.2f", total));
            model.addAttribute("user", user);

            return "user/invoice";
        }

        return "redirect:/";
    }
    // endregion

    // region 13. /"add-review/"
    /**
     * Shows the form to post a new review for a selected tour.
     */
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
    // endregion

    // region 14. "/review-user"
    /**
     * Displays a paginated view of all reviews authored by the current user.
     */
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
    // endregion

    // region 15. /"mis-reviews/{id}/edit-review"
    /**
     * Displays the form to modify an existing review.
     */
    @GetMapping("/mis-reviews/{id}/edit-review")
    public String editReview(@PathVariable Long id, Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/";
        }

        Optional<Review> optionalReview = reviewService.findById(id);

        if (optionalReview.isEmpty()) {
            return "redirect:/review-user";
        }

        Review review = optionalReview.get();
        if (!review.getUser().getId().equals(user.getId())) {
            return "redirect:/review-user";
        }

        model.addAttribute("review", review);
        model.addAttribute("tour", review.getTour());

        return "user/edit-review";
    }
    // endregion

    // region 16. "/booking-user"
    /**
     * Retrieves the past closed bookings for the user.
     */
    @GetMapping("/booking-user")
    public String myBookings(Principal principal,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            return "redirect:/";
        }

        Page<Booking> bookingPage = bookingService.findClosedBookingsByUser(user, pageable);

        model.addAttribute("bookings", bookingPage.getContent());
        model.addAttribute("size", pageable.getPageSize());
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("hasNext", bookingPage.hasNext());
        model.addAttribute("nextPage", pageable.getPageNumber() + 1);

        model.addAttribute("hasPrevious", bookingPage.hasPrevious());
        model.addAttribute("previousPage", pageable.getPageNumber() - 1);

        return "user/user-bookings";
    }
    // endregion

    // region 17. "/forgot-password"
    /**
     * Renders the forgot password utility page.
     */
    @GetMapping("/forgot-password")
    public String forgot_password() {
        return "/user/forgot-password";
    }
    // endregion

    // region 18. "/admin-login"
    /**
     * Displays the administrative login portal.
     */
    @GetMapping("/admin-login")
    public String adminLogin(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error != null);
        return "user/admin-login";
    }
    // endregion

    // region 19. "/user/{id}/image"
    /**
     * Retrieves the profile picture data to be rendered by the view.
     */
    @GetMapping("/user/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null && user.getProfilePicture() != null) {   // check user exists and has image
            byte[] imageBytes = user.getProfilePicture().getImageFile();   // extract bytes
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/png")
                    .body(imageBytes);
        }
        return ResponseEntity.notFound().build();
    }
    // endregion

    // region 20. "/guides"
    /**
     * Shows a list of available guides on the public facing site.
     */
    @GetMapping("/guides")
    public String guides(Model model) {

        List<Guide> guides = guideService.findAll();

        if (guides.size() > 6) {
            guides = guides.subList(0, 6);
        }

        model.addAttribute("guides", guides);

        return "user/guides";
    }

    // endregion
    // endregion

    // region =========== PostMapping =================
    // region 1. "/register"
    /**
     * Handles new user registration via the public sign-up form.
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegisterDTO newUser,
            BindingResult result,
            @RequestParam(required = false) MultipartFile imageFile, // pfp
            Model model) throws IOException {

        // if there are errors, return to the page
        if (result.hasErrors()) {
            return "user/register";
        }

        // check email in use
        if (userService.emailExists(newUser.email())) {
            model.addAttribute("errorMessage", "El correo electrónico ya está registrado en otra cuenta.");
            return "user/register";
        }

        // check main phone in use
        if (userService.phoneExists(newUser.mainPhone())) {
            model.addAttribute("errorMessage", "El teléfono principal ya está en uso.");
            return "user/register";
        }

        // check secondary phone (if sent) is used
        String secondaryPhone = newUser.secondaryPhone();
        if (secondaryPhone != null && !secondaryPhone.trim().isEmpty()) {
            if (userService.phoneExists(secondaryPhone)) {
                model.addAttribute("errorMessage", "El teléfono secundario ya está en uso por otra cuenta.");
                return "user/register";
            }
            // check main and secondary arent same
            if (newUser.mainPhone().equals(secondaryPhone)) {
                model.addAttribute("errorMessage", "El teléfono principal y secundario no pueden ser el mismo.");
                return "user/register";
            }
        }

        User user = new User();
        user.setName(newUser.name());
        user.setLastName(newUser.lastName());
        user.setEmail(newUser.email());
        user.setMainPhone(newUser.mainPhone());
        user.setSecondaryPhone(secondaryPhone);

        // encode password
        user.setPassword(passwordEncoder.encode(newUser.password()));

        // fill other fields
        user.setRoles(java.util.List.of("USER"));
        user.setEnabled(true);
        user.setMoneySpent(0.0);

        // pfp logic
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // if image was attached, store it2
                user.setProfilePicture(new Image (imageFile.getBytes()));
            } else {
                // if no image attached, generate default
                byte[] avatar = userService.generateDefaultAvatar("Usuario", newUser.name(),
                        new Color(13, 110, 253));
                user.setProfilePicture (new Image (avatar));
            }
        } catch (IOException e) {
            // if error reading file, set default
            e.printStackTrace();
            byte[] avatar = userService.generateDefaultAvatar("Usuario", newUser.name(), new Color(13, 110, 253));
            user.setProfilePicture (new Image (avatar));
        }

        userService.save(user); // save user

        // notificaction: user creation via user (registration page)
        notificationService.notify("Nuevo usuario registrado: " + user.getName(), "fas fa-user-plus", "bg-success");

        return "redirect:/login"; // go login
    }
    // endregion

    // region 2. "/profile/update"
    /**
     * Processes profile updates initiated by the user.
     */
    @PostMapping("/profile/update")
    public String updateProfile(Principal principal,
            HttpServletRequest request,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String mainPhone,
            @RequestParam(required = false) String secondaryPhone,
            @RequestParam(required = false) String oldPassword, // not in user, so requested separately
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam String clickaction,
            Model model) throws IOException, ServletException {

        // get user logged in
        User user = userService.findByEmail(principal.getName());

        // flag for password change
        boolean passwordChanged = false;

        // process user deletion
        if ("delete".equals(clickaction)) {
            userService.delete(user);
            // notification: user deletion via admin (delete button in users page)
            notificationService.notify("Usuario " + user.getName() + " ha eliminado su cuenta", "fas fa-user-minus",
                    "bg-warning");
            return "redirect:/login";
        }

        // clean invisible spaces
        mainPhone = mainPhone != null ? mainPhone.trim() : "";
        secondaryPhone = secondaryPhone != null ? secondaryPhone.trim() : "";

        // check if main phone repeated
        if (!mainPhone.equals(user.getMainPhone()) && userService.phoneExists(mainPhone)) {
            model.addAttribute("errorMessage", "El teléfono principal ya está en uso por otro usuario.");
            model.addAttribute("currentUser", user);
            return "user/profile";
        }

        // check if secondary phone repeated
        if (!secondaryPhone.isEmpty()) {
            if (!secondaryPhone.equals(user.getSecondaryPhone()) && userService.phoneExists(secondaryPhone)) {
                model.addAttribute("errorMessage", "El teléfono secundario ya está en uso.");
                model.addAttribute("currentUser", user);
                return "user/profile";
            }
            // case: main == secondary
            if (mainPhone.equals(secondaryPhone)) {
                model.addAttribute("errorMessage", "El teléfono principal y secundario no pueden ser iguales.");
                model.addAttribute("currentUser", user);
                return "user/profile";
            }
        }

        // password validation
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            // old password matches
            if (oldPassword == null || oldPassword.isEmpty()
                    || !passwordEncoder.matches(oldPassword, user.getPassword())) {
                model.addAttribute("errorMessage", "La contraseña antigua es incorrecta.");
                model.addAttribute("currentUser", user);
                return "user/profile";
            }

            // new and confirmation match
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Las nuevas contraseñas no coinciden.");
                model.addAttribute("currentUser", user);
                return "user/profile";
            }

            // encrypt and store
            user.setPassword(passwordEncoder.encode(newPassword));
            passwordChanged = true;
        }

        // update info
        user.setName(name);
        user.setLastName(lastName);
        user.setMainPhone(mainPhone);
        user.setSecondaryPhone(secondaryPhone);

        // process new image (if uploaded)
        if (!imageFile.isEmpty()) {
            user.setProfilePicture(new Image (imageFile.getBytes()));
        }

        // save changes
        userService.save(user);

        // password was changed, logout and go to login page
        if (passwordChanged) {
            request.logout();
            return "redirect:/login?changed=true";
        }

        // return to profile page
        return "redirect:/profile";
    }
    // endregion

    // region 3. "/notifications/read/{id}"
    /**
     * Marks a specific notification as read.
     */
    @PostMapping("/notifications/read/{id}")
    public String markAsRead(@PathVariable Long id, HttpServletRequest request) {
        notificationService.markAsRead(id);
        // reload page
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/admin/index");
    }
    // endregion

    // region 4. "/contact"
    /**
     * Processes form submissions from the contact page.
     */
    @PostMapping("/contact")
    public String handleContactSubmit() {
        return "redirect:/contact?wip=true";
    }
    // endregion

    // region 5. "/notifications/delete/{id}"
    /**
     * Deletes a specific notification from the database.
     */
    @PostMapping("/notifications/delete/{id}")
    public String deleteNotification(@PathVariable Long id, HttpServletRequest request) {
        notificationService.delete(id);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/admin/index");
    }
    // endregion
    // endregion
}
