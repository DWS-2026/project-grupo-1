package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;



@Service // service to fill up users table on app startup
public class DatabaseInitializer {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() { // autoruns after initializing spring context

        // only insert data if users table completely empty
        if (userRepository.count() == 0) {

            // create standard client user
            User user = new User();
            user.setName("Client");
            user.setLastName("Normal");
            user.setEmail("user@test.com");
            user.setPassword("1234");
            user.setRoles(List.of("USER"));
            userRepository.save(user);

            // create admin user
            User admin = new User();
            admin.setName("Boss");
            admin.setLastName("Administrator");
            admin.setEmail("admin@test.com");
            admin.setPassword("1234");
            admin.setRoles(List.of("USER", "ADMIN"));
            userRepository.save(admin);

            // log creation success
            System.out.println(">>> Sample users (USER and ADMIN) have been successfully inserted.");
        }
    }
}