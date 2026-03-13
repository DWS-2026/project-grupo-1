package es.codeurjc.daw.library.controller;



import org.springframework.beans.factory.annotation.Autowired; // to inject user service
import org.springframework.stereotype.Controller; // to define class as controller
import org.springframework.ui.Model; // to pass data from controller to mustache view template
import org.springframework.web.bind.annotation.*; // to add annotation for HTTP method mapping to java methods
import org.springframework.web.multipart.MultipartFile; // handles file uploads (pfp) from http forms

import es.codeurjc.daw.library.model.User; // to be able to work with user entity
import es.codeurjc.daw.library.service.UserService; // to call its methods
import es.codeurjc.daw.library.service.NotificationService;

// to call user service pfp generation
import java.awt.Color;
import java.io.IOException;
import java.util.List;






/**
 * controller to manage administrative tasks for users
 * all routes prefixed with /admin/users.
 */
@Controller
@RequestMapping ("/admin/users") // group user management routes
public class UserController {
    @Autowired // to be able to use user service methods
    private UserService userService;
    @Autowired // for password protection before db storage
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired // to generate notifications
    private NotificationService notificationService;



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
            updatedUser.setProfilePicture (imageFile.getBytes());
        }

        userService.save (updatedUser);
        notificationService.notify ("Usuario actualizado: " + name, "fas fa-user-edit", "bg-info");

        return "redirect:/admin/users";
    }



    // deletes user
    @PostMapping ("/delete/{id}")
    public String deleteUser (@PathVariable Long id) {
        // search target user by id
        User user = userService.findById(id);

        // if user exists, delete him
        if (user != null) {
            userService.delete (user);
        }

        // notification user deletion
        notificationService.notify ("Usuario eliminado: " + user.getName(), "fas fa-user", "bg-danger");


        // redirect to users
        return "redirect:/admin/users";
    }


    
    // display user creation form
    @GetMapping ("/add")
    public String showAddUserForm() {
        return "admin/user-add";
    }



    /**
     * processes creation of new user
     * ensures password is encrypted and handles default avatar generation if no image is uploaded
     */
    @PostMapping ("/add")
    public String saveUser (@RequestParam String name,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String mainPhone,
                           @RequestParam String password,
                           @RequestParam double moneySpent,
                           @RequestParam boolean enabled,
                            @RequestParam(required = false) String secondaryPhone,
                           @RequestParam(required = false) MultipartFile imageFile,
                            Model model) throws IOException {

        // check email in use
        if (userService.emailExists (email)) {
            model.addAttribute ("errorMessage", "El correo electrónico ya está registrado en otra cuenta.");
            return "admin/user-add";
        }

        // check main phone in use
        if (userService.phoneExists (mainPhone)) {
            model.addAttribute ("errorMessage", "El teléfono principal ya está en uso.");
            return "admin/user-add";
        }

        // check secondary phone (if sent) is used
        if (secondaryPhone != null && !secondaryPhone.trim().isEmpty()) {
            if (userService.phoneExists (secondaryPhone)) {
                model.addAttribute ("errorMessage", "El teléfono secundario ya está en uso por otra cuenta.");
                return "admin/user-add";
            }
            // check main and secondary arent same
            if (mainPhone.equals (secondaryPhone)) {
                model.addAttribute ("errorMessage", "El teléfono principal y secundario no pueden ser el mismo.");
                return "admin/user-add";
            }
        }

        // if no repetitions, create user
        User newUser = new User();
        newUser.setName (name);
        newUser.setLastName (lastName);
        newUser.setEmail (email);
        newUser.setMainPhone (mainPhone);
        newUser.setSecondaryPhone (secondaryPhone);
        newUser.setMoneySpent (moneySpent);
        newUser.setEnabled (enabled);
        newUser.setRoles (java.util.Arrays.asList("USER"));

        // apply password protection
        newUser.setPassword(passwordEncoder.encode(password));

        // if an image is added, store it too
        if (imageFile != null && !imageFile.isEmpty()) {
            byte[] imageBytes = imageFile.getBytes();
            newUser.setProfilePicture(imageBytes);
        }

        else {
            // if no picture, generate default
            byte[] avatar = userService.generateDefaultAvatar("Usuario", name, new Color(13, 110, 253));
            newUser.setProfilePicture(avatar);
        }

        userService.save (newUser);

        // notification user creation
        notificationService.notify ("Usuario creado: " + name, "fas fa-user", "bg-success");

        return "redirect:/admin/users";
    }
}