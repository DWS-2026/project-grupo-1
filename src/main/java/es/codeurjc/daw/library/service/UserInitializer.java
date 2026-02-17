package es.codeurjc.daw.library.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;

@Component
@Order(1)
public class UserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() != 0) return;

        createUser("Luis", "Coca", "luis@email.com", "1234");
        createAdmin("Pablo", "Admin", "admin@apexexpeditions.com", "1234");

        System.out.println(">>> Users initialized");
    }

    private void createUser(String name, String lastName,
                            String email, String password) {

        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Arrays.asList("USER"));

        userRepository.save(user);
    }

    private void createAdmin(String name, String lastName,
                             String email, String password) {

        User admin = new User();
        admin.setName(name);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRoles(Arrays.asList("USER", "ADMIN"));

        userRepository.save(admin);
    }
}