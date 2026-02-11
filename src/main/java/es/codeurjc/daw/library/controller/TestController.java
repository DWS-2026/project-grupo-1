package es.codeurjc.daw.library.controller;

import es.codeurjc.daw.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/TEST/TEST_users")
    public String showTestTable(Model model) {
        // Obtenemos los usuarios y los pasamos al HTML
        model.addAttribute("users", userRepository.findAll());

        // Esta ruta coincide con tu tree: templates/TEST/TEST_users.html
        return "TEST/TEST_users";
    }
}