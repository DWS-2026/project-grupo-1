package es.codeurjc.daw.library.service;



import es.codeurjc.daw.library.model.User; // user dependency
import es.codeurjc.daw.library.repository.UserRepository; // user dependency

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // for password security

import java.util.List; // for user lists



@Service // service to fill up users table on app startup
public class DatabaseInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostConstruct
    public void init() { // autoruns after initializing spring context

        // only insert data if users table completely empty
        if (userRepository.count() == 0) {

            // create standard client user
            User user = new User();
            user.setName("Client");
            user.setLastName("Normal");
            user.setEmail("user@test.com");
            user.setPassword(passwordEncoder.encode("1234"));
            user.setRoles(List.of("USER"));
            userRepository.save(user);

            // ADMIN USER CREATION START =====================================
            // pablo admin creation start
            User adminPablo = new User();
            adminPablo.setName("Pablo");
            adminPablo.setLastName("Apellido");
            adminPablo.setEmail("adminPablo@apexexpeditions.com");
            adminPablo.setPassword(passwordEncoder.encode("1234pablo"));
            adminPablo.setRoles(List.of("USER", "ADMIN"));
            userRepository.save(adminPablo);
            // pablo admin creation end

            // javier admin creation start
            User adminJavier = new User();
            adminJavier.setName("Javier");
            adminJavier.setLastName("Apellido");
            adminJavier.setEmail("adminJavier@apexexpeditions.com");
            adminJavier.setPassword(passwordEncoder.encode("1234javier"));
            adminJavier.setRoles(List.of("USER", "ADMIN"));
            userRepository.save(adminJavier);
            // javier admin creation end

            // mario admin creation start
            User adminMario = new User();
            adminMario.setName("Mario");
            adminMario.setLastName("Apellido");
            adminMario.setEmail("adminMario@apexexpeditions.com");
            adminMario.setPassword(passwordEncoder.encode("1234mario"));
            adminMario.setRoles(List.of("USER", "ADMIN"));
            userRepository.save(adminMario);
            // mario admin creation end


            // andres admin creation start
            User adminAndres = new User();
            adminAndres.setName("Andres");
            adminAndres.setLastName("Apellido");
            adminAndres.setEmail("adminAndres@apexexpeditions.com");
            adminAndres.setPassword("1234andres");
            adminAndres.setRoles(List.of("USER", "ADMIN"));
            userRepository.save(adminAndres);
            // andres admin creation end
            // ADMIN USER CREATION END =====================================

            // log creation success
            System.out.println(">>> Sample users successfully inserted.");
        }
    }
}