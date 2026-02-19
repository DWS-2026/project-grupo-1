package es.codeurjc.daw.library.service;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;

// for pfp generation
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import jakarta.annotation.Nullable;




@Component
@Order (1) // first load users
public class UserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run (String... args) {

        // if there are already users, do no thing (ddl-auto is set to create)
        if (userRepository.count() != 0) return;

        // create 5 users
        createUser("Luis", "Coca", "luis@email.com", "1234", "600111222", "910112233",
                120.50, true, LocalDateTime.now().minusDays(5));

        createUser("Ana", "García", "ana@email.com", "1234", "611222333", null,
                450.75, true, LocalDateTime.now().minusMonths(2));

        createUser("Carlos", "López", "carlos@email.com", "1234", "622333444", "922333444",
                0.0, true, LocalDateTime.now().minusDays(1)); // Usuario recién registrado sin compras

        createUser("Marta", "Sánchez", "marta@email.com", "1234", "633444555", "933444555",
                89.90, false, LocalDateTime.now().minusYears(1)); // Usuario con cuenta deshabilitada

        createUser("David", "Martín", "david@email.com", "1234", "644555666", null,
                1500.00, true, LocalDateTime.now().minusMonths(6));

        System.out.println(">>> Users initialized");

        // create admin
        createAdmin("NombreDeAdmin", "ApellidoDeAdmin", "admin@apexexpeditions.com", "1234", "700111222", "800111222");

        System.out.println(">>> Admin initialized");
    }




    //  method to create user
    private void createUser (String name, String lastName, String email, String password, String mainPhone, String secondaryPhone,
                             double moneySpent, boolean enabled, LocalDateTime creationDate) {
        User user = new User(name, lastName, email, passwordEncoder.encode(password), mainPhone, secondaryPhone,
                moneySpent, enabled, creationDate);

        // if user, pfp bg is blue
        String avatar = generateDynamicAvatar ("Usuario", name, new Color(13, 110, 253));
        user.setProfilePicture(avatar);

        user.setRoles(Arrays.asList("USER"));
        userRepository.save(user);
    }




    // method to create admin
    private void createAdmin (String name, String lastName, String email, String password, String mainPhone, String secondaryPhone) {
        User admin = new User(name, lastName, email, passwordEncoder.encode(password), mainPhone, secondaryPhone);

        // if admin, pfp bg is black
        String avatar = generateDynamicAvatar("Admin", name, new Color(0, 0, 0));
        admin.setProfilePicture(avatar);

        admin.setRoles(Arrays.asList("USER", "ADMIN"));
        userRepository.save(admin);
    }




    /**
     * @param roleText first line text (user role)
     * @param nameText second line text (users name)
     * @param bgColor image bg color
     * @return b64 string ready to store in db
     */
    private @Nullable String generateDynamicAvatar(String roleText, String nameText, Color bgColor) {
        int width = 200;
        int height = 200;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));

        FontMetrics metrics = g.getFontMetrics();

        int x1 = (width - metrics.stringWidth(roleText)) / 2;
        int y1 = (height / 2) - 15;
        g.drawString(roleText, x1, y1);

        int x2 = (width - metrics.stringWidth(nameText)) / 2;
        int y2 = (height / 2) + 25;
        g.drawString(nameText, x2, y2);

        g.dispose();

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