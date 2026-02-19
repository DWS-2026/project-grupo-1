package es.codeurjc.daw.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Base64;



@Controller
@RequestMapping("/admin/users") // group user management routes
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // show user list
    @GetMapping
    public String listUsers(Model model) {
        // retrieve all users
        List<User> allUsers = userService.findAll();

        // filter to exclude admin
        List<User> customersOnly = allUsers.stream()
                .filter(user -> !user.getRoles().contains("ADMIN"))
                .toList();

        model.addAttribute("users", customersOnly);
        return "admin/users";
    }

    // load editing form (final path: /admin/users/edit/{id})
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);

        if (user == null) {
            return "redirect:/admin/users"; // if it doesnt exist, return to table
        }

        model.addAttribute("user", user);
        return "admin/user-edit";
    }

    // save form changes (final path: POST /admin/users/edit/{id})
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String name,
                             @RequestParam String lastName,
                             @RequestParam String email,
                             @RequestParam double moneySpent,
                             @RequestParam boolean enabled,
                             @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        User existingUser = userService.findById(id);

        // update as requested if user exist
        if (existingUser != null) {
            existingUser.setName(name);
            existingUser.setLastName(lastName);
            existingUser.setEmail(email);
            existingUser.setMoneySpent(moneySpent);
            existingUser.setEnabled(enabled);

            // update image
            if (imageFile != null && !imageFile.isEmpty()) {
                byte[] bytes = imageFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(bytes);
                existingUser.setProfilePicture(base64Image);
            }
        }

            userService.save(existingUser);

        return "redirect:/admin/users";
    }


    // deletes user
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        // search target user by id
        User user = userService.findById(id);

        // if user exists, delete him
        if (user != null) {
            userService.delete(user);
        }

        // redirect to users
        return "redirect:/admin/users";
    }

    
    // display user creation form
    @GetMapping("/add")
    public String showAddUserForm() {
        return "admin/user-add";
    }


    // process user creation request (post)
    @PostMapping("/add")
    public String saveUser(@RequestParam String name,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam String mainPhone,
                           @RequestParam String secondaryPhone,
                           @RequestParam String password,
                           @RequestParam double moneySpent,
                           @RequestParam boolean enabled,
                           @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        // create the instance
        User newUser = new User();
        newUser.setName(name);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setMainPhone(mainPhone);
        newUser.setSecondaryPhone(secondaryPhone);
        newUser.setMoneySpent(moneySpent);
        newUser.setEnabled(enabled);
        newUser.setRoles(java.util.Arrays.asList("USER"));

        // apply password protection
        newUser.setPassword(passwordEncoder.encode(password));

        // if an image is added, store it too
        if (imageFile != null && !imageFile.isEmpty()) {
            byte[] bytes = imageFile.getBytes();
            String base64Image = java.util.Base64.getEncoder().encodeToString(bytes);
            newUser.setProfilePicture(base64Image);
        }

        else {
            // if no picture, generate default
            String avatar = userService.generateDefaultAvatar("Usuario", name, new Color(13, 110, 253));
            newUser.setProfilePicture(avatar);
        }

        userService.save(newUser);

        return "redirect:/admin/users";
    }
}