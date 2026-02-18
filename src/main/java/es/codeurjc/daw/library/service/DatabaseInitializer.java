package es.codeurjc.daw.library.service;




import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.User; // user dependency
import es.codeurjc.daw.library.repository.ReviewRepository;
import es.codeurjc.daw.library.repository.TourRepository;
import es.codeurjc.daw.library.repository.GuideRepository;
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

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private GuideRepository guideRepository;



    @PostConstruct
    public void init() { // autoruns after initializing spring context

        // only insert data if users table completely empty
        if (userRepository.count() == 0) {
            System.out.println (">>> Initializing users in db");

            // normal user creation
            createUser ("Luis", "Coca", "luiscoca@email.com", "1234luis", "600111222", "910112233");
            createUser ("Ana", "Fernández", "anafernandez@email.com", "1234ana", "600444555", "910445566");
            createUser ("Carlos", "Reberte", "carlosreberte@email.com", "1234carlos", "600777888", "910778899");
            createUser ("Test", "Testing", "testtesting@email.com", "1234test", "600000000", "900000000");


            // admin creation
            createAdmin ("Pablo", "ApellidoDePablo", "adminPablo@apexexpeditions.com", "1234pablo", "700111222", "800111222");
            createAdmin ("Javier", "ApellidoDeJavier", "adminJavier@apexexpeditions.com", "1234javier", "700333444", "800333444");
            createAdmin ("Mario", "ApellidoDeMario", "adminMario@apexexpeditions.com", "1234mario", "700555666", "800555666");
            createAdmin ("Andres", "ApellidoDeAndres", "adminAndres@apexexpeditions.com", "1234andres", "700777888", "800777888");

            System.out.println (">>> users created successfully.");
        }

        if (tourRepository.count() == 0) {
            createTour("Viaje al futuro", "imagen1.jpg",
                    "Explora ciudades futuristas...",
                    4349.00);

            createTour("Volcán Krakatoa",
                    "imagen2.jpg",
                    "Explora el interior del volcán...",
                    1649.00);
        }

        if (reviewRepository.count() == 0) {

            User user = userRepository.findAll().get(0);
            Tour tour = tourRepository.findAll().get(0);

            createReview(user, tour, 5, "Una experiencia inolvidable.");
            createReview(user, tour, 4, "Muy buen tour, repetiría.");
        }

        if (guideRepository.count() == 0) {
            Tour t1 = tourRepository.findAll().get(0);

            createGuide("Mario", "Ortiz Lopo", 199.99, t1);
            createGuide("Pablo", "Sánchez Martín", 149.99, t1);
            createGuide("Javier", "Hernández Campano", 199.99, t1);
            createGuide("Andrés", "Sánchez Nogales", 149.99, t1);
        }

    }

    // method to create normal user
    private void createUser (String name, String lastName, String email, String password, String mainPhone, String secondaryPhone) {
        User user = new User();
        user.setName (name);
        user.setLastName (lastName);
        user.setEmail (email);
        user.setPassword (passwordEncoder.encode (password));
        user.setMainPhone(mainPhone);
        user.setSecondaryPhone(secondaryPhone);
        user.setRoles (List.of ("USER"));

        // blue avatar for normal user
        String avatar = generateDynamicAvatar ("User", name, new Color (13, 110, 253));
        user.setProfilePicture (avatar);

        userRepository.save (user);
    }


    // method to create admin user
    private void createAdmin (String name, String lastName, String email, String password, String mainPhone, String secondaryPhone) {
        User admin = new User();
        admin.setName (name);
        admin.setLastName (lastName);
        admin.setEmail (email);
        admin.setPassword (passwordEncoder.encode (password));
        admin.setMainPhone(mainPhone);
        admin.setSecondaryPhone(secondaryPhone);
        admin.setRoles (List.of ("USER", "ADMIN"));
        // red avatar for admin
        admin.setProfilePicture (generateDynamicAvatar ("Admin", name, new Color (0, 0, 0)));
        userRepository.save (admin);
    }

    // ================= TOUR METHOD =================

    private void createTour(String name, String image,
                            String description, double price) {

        Tour tour = new Tour();
        tour.setName(name);
        tour.setImage(image);
        tour.setDescription(description);
        tour.setPrice(price);

        tourRepository.save(tour);
    }


    private void createReview(User user, Tour tour, int rating, String description) {

        Review review = new Review();
        review.setUser(user);
        review.setTour(tour);
        review.setRating(rating);
        review.setDescription(description);

        reviewRepository.save(review);
    }


    private void createGuide(String name, String lastName, double price, Tour tour) {
        Guide g = new Guide();
        g.setName(name);
        g.setLastName(lastName);
        g.setPrice(price);
        g.setTour(tour);
        guideRepository.save(g);
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