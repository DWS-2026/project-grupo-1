package es.codeurjc.daw.library.controller;






// region =========== imports =================
import es.codeurjc.daw.library.model.Image;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; // to inject user service
import org.springframework.stereotype.Controller; // to define class as controller
import org.springframework.ui.Model; // to pass data from controller to mustache view template
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; // to add annotation for HTTP method mapping to java methods
import org.springframework.web.multipart.MultipartFile; // handles file uploads (pfp) from http forms

import es.codeurjc.daw.library.model.User; // to be able to work with user entity
import es.codeurjc.daw.library.service.UserService; // to call its methods
import es.codeurjc.daw.library.service.NotificationService;

// to call user service pfp generation
import java.awt.Color;
import java.io.IOException;
import java.util.List;
// endregion






/**
 * controller to manage administrative tasks for users
 * all routes prefixed with /admin/users.
 */
@Controller
@RequestMapping ("/admin/users") // group user management routes
public class UserController {
    // region =========== Autowired =================
    @Autowired // to be able to use user service methods
    private UserService userService;
    @Autowired // for password protection before db storage
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired // to generate notifications
    private NotificationService notificationService;
    // endregion





    // region =========== GetMapping =================
    // region 1. listUsers
    /**
     * displays list of users
     * fetches all users filtering out those with ADMIN role
     */
    @GetMapping
    public String listUsers (Model model) {
        // retrieve all users
        List<User> allUsers = userService.findAll();

        // filter to exclude admin
        List<User> customersOnly = allUsers.stream()
                .filter (user -> !user.getRoles().contains("ADMIN"))
                .toList();

        model.addAttribute ("users", customersOnly);
        return "admin/users";
    }
    // endregion



    // region 2. editUser
    /**
     * prepares editing form for specific user
     * @param id db id of user to edit
     */
    @GetMapping ("/edit/{id}")
    public String editUser (@PathVariable Long id, Model model) {
        // search for target user
        User user = userService.findById (id);

        // if it doesnt exist, return to user table page
        if (user == null) {
            return "redirect:/admin/users";
        }

        model.addAttribute ("user", user);
        return "admin/user-edit";
    }
    // endregion



    // region 3. showAddUserForm
    // display user creation form
    @GetMapping ("/add")
    public String showAddUserForm() {
        return "admin/user-add";
    }
    // endregion
    // endregion





    // region =========== PostMapping =================
    // region 1. updateUser
    /**
     * processes update request from edit form
     * imageFile is optional. If provided, converted to base64 to be stored as string in db
     */
    @PostMapping ("/edit/{id}")
    public String updateUser (@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam String lastName,
                             @RequestParam String email,
                              @RequestParam String mainPhone,
                              @RequestParam(required = false) String secondaryPhone,
                             @RequestParam double moneySpent,
                             @RequestParam boolean enabled,
                              @RequestParam(required = false) String newPassword,
                             @RequestParam(required = false) MultipartFile imageFile,
                              Model model) throws IOException {

        // check user exists
        User updatedUser = userService.findById (id);
        if (updatedUser == null) {
            return "redirect:/admin/users";
        }

        // check email
        if (!email.equals (updatedUser.getEmail()) && userService.emailExists (email)) {
            model.addAttribute ("errorMessage", "El correo electrónico ya está registrado por otro usuario.");
            model.addAttribute ("user", updatedUser);
            return "admin/user-edit";
        }

        // check main phone
        if (!mainPhone.equals (updatedUser.getMainPhone()) && userService.phoneExists (mainPhone)) {
            model.addAttribute ("errorMessage", "El teléfono principal ya está en uso por otro usuario.");
            model.addAttribute ("user", updatedUser);
            return "admin/user-edit";
        }

        // check secondary phone
        if (secondaryPhone != null && !secondaryPhone.trim().isEmpty()) {
            // case already in use
            if (!secondaryPhone.equals (updatedUser.getSecondaryPhone()) && userService.phoneExists (secondaryPhone)) {
                model.addAttribute ("errorMessage", "El teléfono secundario ya está en uso por otro usuario.");
                model.addAttribute ("user", updatedUser);
                return "admin/user-edit";
            }
            // case primary is secondary too
            if (mainPhone.equals (secondaryPhone)) {
                model.addAttribute ("errorMessage", "El teléfono principal y secundario no pueden ser el mismo.");
                model.addAttribute ("user", updatedUser);
                return "admin/user-edit";
            }
        }
        // if no errors
        updatedUser.setName(name);
        updatedUser.setLastName(lastName);
        updatedUser.setEmail(email);
        updatedUser.setMainPhone(mainPhone);
        updatedUser.setSecondaryPhone(secondaryPhone);
        updatedUser.setMoneySpent(moneySpent);
        updatedUser.setEnabled(enabled);
        // encode password
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            updatedUser.setPassword (passwordEncoder.encode(newPassword));
        }
        // set pfp
        if (imageFile != null && !imageFile.isEmpty()) {
            updatedUser.setProfilePicture (new Image (imageFile.getBytes()));
        }

        userService.save (updatedUser);
        notificationService.notify ("Usuario actualizado: " + name, "fas fa-user-edit", "bg-info");

        return "redirect:/admin/users";
    }
    // endregion



    // region 2. deleteUser
    // deletes user
    @PostMapping ("/delete/{id}")
    public String deleteUser (@PathVariable Long id) {
        // search target user by id
        User user = userService.findById(id);

        // if user exists, delete him
        if (user != null) {
            userService.delete (user);
        }

        // notification: user deletion via admin (delete button in users page)
        notificationService.notify("Admin ha eliminado al usuario: " + user.getName(), "fas fa-user-minus", "bg-warning");


        // redirect to users
        return "redirect:/admin/users";
    }
    // endregion



    // region 3. saveUser
    /**
     * processes creation of new user
     * ensures password is encrypted and handles default avatar generation if no image is uploaded
     */
    @PostMapping ("/add")
    public String saveUser (@Valid @ModelAttribute User newUser,
                            BindingResult result,
                            @RequestParam(required = false) MultipartFile imageFile,
                            Model model) throws IOException {

        // return if errors
        if (result.hasErrors()) {
            return "admin/user-add";
        }

        // check email in use
        if (userService.emailExists (newUser.getEmail())) {
            model.addAttribute ("errorMessage", "El correo electrónico ya está registrado en otra cuenta.");
            return "admin/user-add";
        }

        // check main phone in use
        if (userService.phoneExists (newUser.getMainPhone())) {
            model.addAttribute ("errorMessage", "El teléfono principal ya está en uso.");
            return "admin/user-add";
        }

        // check secondary phone (if sent) is used
        if (newUser.getSecondaryPhone() != null && !newUser.getSecondaryPhone().trim().isEmpty()) {
            if (userService.phoneExists (newUser.getSecondaryPhone())) {
                model.addAttribute ("errorMessage", "El teléfono secundario ya está en uso por otra cuenta.");
                return "admin/user-add";
            }
            // check main and secondary arent same
            if (newUser.getMainPhone().equals (newUser.getSecondaryPhone())) {
                model.addAttribute ("errorMessage", "El teléfono principal y secundario no pueden ser el mismo.");
                return "admin/user-add";
            }
        }

        // if no repetitions, apply protection
        newUser.setRoles (java.util.Arrays.asList("USER"));
        newUser.setPassword (passwordEncoder.encode(newUser.getPassword()));

        // if an image is added, store it too
        if (imageFile != null && !imageFile.isEmpty()) {
            byte[] imageBytes = imageFile.getBytes();
            newUser.setProfilePicture (new Image (imageBytes));
        }

        else {
            // if no picture, generate default
            byte[] avatar = userService.generateDefaultAvatar("Usuario", newUser.getName(), new Color(13, 110, 253));
            newUser.setProfilePicture (new Image (avatar));
        }

        userService.save (newUser);

        // notificaction: user creation via admin (user-add page)
        notificationService.notify ("Admin ha creado al usuario: " + newUser.getName(), "fas fa-user-plus", "bg-success");

        return "redirect:/admin/users";
    }
    // endregion
    // endregion
}