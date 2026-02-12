package es.codeurjc.daw.library.service;




import es.codeurjc.daw.library.model.User; // user dependency
import es.codeurjc.daw.library.repository.UserRepository; // user dependency

import jakarta.annotation.PostConstruct;

import org.jspecify.annotations.Nullable; // for pfp generation

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // for password security

import java.util.List; // for user lists

// for pfp generation
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;




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
            System.out.println (">>> Initializing users in db");

            // normal user creation
            createUser ("Luis", "Coca", "luiscoca@email.com", "1234luis");
            createUser ("Ana", "Fernández", "anafernandez@email.com", "1234ana");
            createUser ("Carlos", "Reberte", "carlosreberte@email.com", "1234carlos");

            // admin creation
            createAdmin ("Pablo", "Apellido", "adminPablo@apexexpeditions.com", "1234pablo");
            createAdmin ("Javier", "Apellido", "adminJavier@apexexpeditions.com", "1234javier");
            createAdmin ("Mario", "Apellido", "adminMario@apexexpeditions.com", "1234mario");
            createAdmin ("Andres", "Apellido", "adminAndres@apexexpeditions.com", "1234andres");

            System.out.println (">>> users created successfully.");
        }
    }


    // method to create normal user
    private void createUser (String name, String lastName, String email, String password) {
        User user = new User();
        user.setName (name);
        user.setLastName (lastName);
        user.setEmail (email);
        user.setPassword (passwordEncoder.encode (password));
        user.setRoles (List.of ("USER"));

        // blue avatar for normal user
        String avatar = generateDynamicAvatar ("User", name, new Color (13, 110, 253));
        user.setProfilePicture (avatar);

        userRepository.save (user);
    }


    // method to create admin user
    private void createAdmin (String name, String lastName, String email, String password) {
        User admin = new User();
        admin.setName (name);
        admin.setLastName (lastName);
        admin.setEmail (email);
        admin.setPassword (passwordEncoder.encode (password));
        admin.setRoles (List.of ("USER", "ADMIN"));
        // red avatar for admin
        admin.setProfilePicture (generateDynamicAvatar ("Admin", name, new Color (220, 53, 69)));
        userRepository.save (admin);
    }


    /**
     * @param roleText first line text (eg. "Admin")
     * @param nameText second line text (eg. "Pablo")
     * @param bgColor image bg color
     * @return b64 string ready to store in db
     */
    // method to create default pfp
    private @Nullable String generateDynamicAvatar (String roleText, String nameText, Color bgColor) {
        // size
        int width = 200;
        int height = 200;

        // bufferedimage creation
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // antianalising to avoid pixelation
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // bg filling
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);

        // font config
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));

        // text centering
        FontMetrics metrics = g.getFontMetrics();

        // adding line 1: role
        int x1 = (width - metrics.stringWidth(roleText)) / 2;
        int y1 = (height / 2) - 15; // Un poco más arriba del centro
        g.drawString(roleText, x1, y1);

        // adding line 2: name
        int x2 = (width - metrics.stringWidth(nameText)) / 2;
        int y2 = (height / 2) + 25; // Un poco más abajo del centro
        g.drawString(nameText, x2, y2);

        // free up resources
        g.dispose();

        // convert to b64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}