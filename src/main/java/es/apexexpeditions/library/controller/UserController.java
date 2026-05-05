package es.apexexpeditions.library.controller;






// region =========== imports =================
import es.apexexpeditions.library.dto.user.UserRequestDTO;
import es.apexexpeditions.library.dto.user.UserUpdateDTO;
import es.apexexpeditions.library.model.Image;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; // to inject user service
import org.springframework.stereotype.Controller; // to define class as controller
import org.springframework.ui.Model; // to pass data from controller to mustache view template
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; // to add annotation for HTTP method mapping to java methods
import org.springframework.web.multipart.MultipartFile; // handles file uploads (pfp) from http forms

import es.apexexpeditions.library.model.User; // to be able to work with user entity
import es.apexexpeditions.library.service.UserService; // to call its methods
import es.apexexpeditions.library.service.NotificationService;

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
     * imageFile is optional. If provided, stored db
     * patched for a03 and a 05
     */
    @PostMapping ("/edit/{id}")
    public String updateUser (@PathVariable Long id,
                              @Valid @ModelAttribute("user") UserUpdateDTO updateData,   // use dto
                              BindingResult bindingResult,
                              @RequestParam(required = false) String newPassword,
                              @RequestParam(required = false) MultipartFile imageFile,
                              Model model) {

        // check user exists
        User updatedUser = userService.findById (id);
        if (updatedUser == null) {
            return "redirect:/admin/users";
        }

        // block giant texts
        if (bindingResult.hasErrors()) {
            model.addAttribute ("errorMessage", "Invalid data: check character limit.");
            model.addAttribute ("user", updatedUser);
            return "admin/user-edit";
        }

        try {
            // check email
            if (!updateData.email().equals(updatedUser.getEmail()) && userService.emailExists(updateData.email())) {
                model.addAttribute ("errorMessage", "El correo electrónico ya está registrado por otro usuario.");
                model.addAttribute ("user", updatedUser);
                return "admin/user-edit";
            }

            // check main phone
            if (!updateData.mainPhone().equals(updatedUser.getMainPhone()) && userService.phoneExists(updateData.mainPhone())) {
                model.addAttribute ("errorMessage", "El teléfono principal ya está en uso por otro usuario.");
                model.addAttribute ("user", updatedUser);
                return "admin/user-edit";
            }

            // check secondary phone
            if (updateData.secondaryPhone() != null && !updateData.secondaryPhone().trim().isEmpty()) {
                if (!updateData.secondaryPhone().equals(updatedUser.getSecondaryPhone()) && userService.phoneExists(updateData.secondaryPhone())) {
                    model.addAttribute ("errorMessage", "El teléfono secundario ya está en uso por otro usuario.");
                    model.addAttribute ("user", updatedUser);
                    return "admin/user-edit";
                }
                if (updateData.mainPhone().equals(updateData.secondaryPhone())) {
                    model.addAttribute ("errorMessage", "El teléfono principal y secundario no pueden ser el mismo.");
                    model.addAttribute ("user", updatedUser);
                    return "admin/user-edit";
                }
            }

            // if no errors, assign validated DTO data
            updatedUser.setName(updateData.name());
            updatedUser.setLastName(updateData.lastName());
            updatedUser.setEmail(updateData.email());
            updatedUser.setMainPhone(updateData.mainPhone());
            updatedUser.setSecondaryPhone(updateData.secondaryPhone());
            updatedUser.setMoneySpent(updateData.moneySpent() != null ? updateData.moneySpent() : 0.0);
            updatedUser.setEnabled(updateData.enabled() != null ? updateData.enabled() : false);

            // encode password
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                updatedUser.setPassword (passwordEncoder.encode(newPassword));
            }

            // set pfp
            if (imageFile != null && !imageFile.isEmpty()) {
                String contentType = imageFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    model.addAttribute ("errorMessage", "El archivo debe ser una imagen válida.");
                    model.addAttribute ( "user", updatedUser);
                    return "admin/user-edit";
                }
                updatedUser.setProfilePicture(new Image(imageFile.getBytes()));
            }

            userService.save (updatedUser);
            notificationService.notify ("Usuario actualizado: " + updateData.name(), "fas fa-user-edit", "bg-info");

        } catch (Exception e) {
            // error capture (prevents zap from detecting server collapse)
            model.addAttribute("errorMessage", "Error interno al procesar la actualización.");
            model.addAttribute("user", updatedUser);
            return "admin/user-edit";
        }
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
            notificationService.notify("Admin ha eliminado al usuario: " + user.getName(), "fas fa-user-minus", "bg-warning");
        }


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
    public String saveUser (@Valid @ModelAttribute UserRequestDTO newUser,
                            BindingResult result,
                            @RequestParam(required = false) MultipartFile imageFile,
                            Model model) throws IOException {

        // return if errors
        if (result.hasErrors()) {
            return "admin/user-add";
        }

        // check email in use
        if (userService.emailExists (newUser.email())) {
            model.addAttribute ("errorMessage", "El correo electrónico ya está registrado en otra cuenta.");
            return "admin/user-add";
        }

        // check main phone in use
        if (userService.phoneExists (newUser.mainPhone())) {
            model.addAttribute ("errorMessage", "El teléfono principal ya está en uso.");
            return "admin/user-add";
        }

        // check secondary phone (if sent) is used
        if (newUser.secondaryPhone() != null && !newUser.secondaryPhone().trim().isEmpty()) {
            if (userService.phoneExists (newUser.secondaryPhone())) {
                model.addAttribute ("errorMessage", "El teléfono secundario ya está en uso por otra cuenta.");
                return "admin/user-add";
            }
            // check main and secondary arent same
            if (newUser.mainPhone().equals (newUser.secondaryPhone())) {
                model.addAttribute ("errorMessage", "El teléfono principal y secundario no pueden ser el mismo.");
                return "admin/user-add";
            }
        }

        User user = new User();
        user.setName (newUser.name());
        user.setLastName (newUser.lastName());
        user.setEmail (newUser.email());
        user.setMainPhone (newUser.mainPhone());
        user.setSecondaryPhone (newUser.secondaryPhone());
        user.setMoneySpent (newUser.moneySpent());
        user.setEnabled (newUser.enabled());

        // if no repetitions, apply protection
        user.setRoles (java.util.List.of("USER"));
        user.setPassword (passwordEncoder.encode(newUser.password()));

        // if an image is added, store it too
        if (imageFile != null && !imageFile.isEmpty()) {
            byte[] imageBytes = imageFile.getBytes();
            user.setProfilePicture (new Image (imageBytes));
        }

        else {
            // if no picture, generate default
            byte[] avatar = userService.generateDefaultAvatar("Usuario", newUser.name(), new Color(13, 110, 253));
            user.setProfilePicture (new Image (avatar));
        }

        userService.save (user);

        // notificaction: user creation via admin (user-add page)
        notificationService.notify ("Admin ha creado al usuario: " + user.getName(), "fas fa-user-plus", "bg-success");

        return "redirect:/admin/users";
    }
    // endregion
    // endregion
}
