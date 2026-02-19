package es.codeurjc.daw.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.UserService;

import java.util.List;




@Controller
@RequestMapping("/admin/users") // group user management routes
public class UserController {

    @Autowired
    private UserService userService;

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
                             @RequestParam boolean enabled) {

        User existingUser = userService.findById(id);

        if (existingUser != null) {
            existingUser.setName(name);
            existingUser.setLastName(lastName);
            existingUser.setEmail(email);
            existingUser.setMoneySpent(moneySpent);
            existingUser.setEnabled(enabled);

            userService.save(existingUser);
        }

        return "redirect:/admin/users";
    }
}