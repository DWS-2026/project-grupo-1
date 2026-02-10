package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInitializer {

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User testUser = new User();
            testUser.setNombre("Pablo");
            testUser.setApellidos("Arch User");
            testUser.setEmail("pablo@test.com");
            testUser.setPassword("1234");
            userRepository.save(testUser);
            System.out.println(">>> Datos de prueba insertados.");
        }
    }
}