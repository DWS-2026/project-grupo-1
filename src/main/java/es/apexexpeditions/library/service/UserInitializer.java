package es.apexexpeditions.library.service;






// region =========== imports =================
import es.apexexpeditions.library.model.Image;   // before awt for priority, to avoid image name issues

import java.time.LocalDateTime;
import java.util.Arrays;
import java.awt.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.repository.UserRepository;
// endregion






// populates db with user samples
@Component
@Order (1) // ensures users are created before other entities that might depend on them
public class UserInitializer implements CommandLineRunner {
    // region =========== autowired =================
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    // endregion





    // region =========== methods =================
    // region 1. run
    // auto-runs on app startup
    @Override
    public void run (String... args) {
        // if there are already users, do nothing (ddl-auto is set to create)
        if (userRepository.count() != 0) return;

        // create 5 users
        createUser ("Luis", "Coca", "luis@email.com", "1234", "600111222", "910112233",
                120.50, true, LocalDateTime.now().minusDays(5));
        createUser ("Ana", "García", "ana@email.com", "1234", "611222333", null,
                450.75, true, LocalDateTime.now().minusMonths(2));
        createUser ("Carlos", "López", "carlos@email.com", "1234", "622333444", "922333444",
                0.0, true, LocalDateTime.now().minusDays(1));
        createUser ("Marta", "Sánchez", "marta@email.com", "1234", "633444555", "933444555",
                89.90, false, LocalDateTime.now().minusYears(1)); // Usuario con cuenta deshabilitada
        createUser ("David", "Martín", "david@email.com", "1234", "644555666", null,
                1500.00, true, LocalDateTime.now().minusMonths(6));
        System.out.println (">>> Users initialized");

        // create admin
        createAdmin ("NombreAdmin", "ApellidoAdmin", "admin@apexexpeditions.com", "1234", "700111222", "800111222");
        System.out.println (">>> Admin initialized");
    }
    // endregion



    // region 2. createuser
    //  method to create user
    private void createUser (String name, String lastName, String email, String password, String mainPhone, String secondaryPhone,
                             double moneySpent, boolean enabled, LocalDateTime creationDate) {
        User user = new User (name, lastName, email, passwordEncoder.encode (password), mainPhone, secondaryPhone,
                moneySpent, enabled, creationDate);

        // if user, pfp bg is blue
        byte[] avatar = userService.generateDefaultAvatar ("Usuario", name, new Color(13, 110, 253));
        user.setProfilePicture (new Image(avatar));

        user.setRoles (Arrays.asList("USER"));
        userRepository.save (user);
    }
    // endregion



    // region 3. createAdmin
    // method to create admin
    private void createAdmin (String name, String lastName, String email, String password, String mainPhone, String secondaryPhone) {
        User admin = new User (name, lastName, email, passwordEncoder.encode (password), mainPhone, secondaryPhone);

        // if admin, pfp bg is black
        byte[] avatar = userService.generateDefaultAvatar ("Admin", name, new Color(0, 0, 0));
        admin.setProfilePicture (new Image (avatar));

        admin.setRoles (Arrays.asList("USER", "ADMIN"));
        userRepository.save (admin);
    }
    // endregion
    // endregion
}