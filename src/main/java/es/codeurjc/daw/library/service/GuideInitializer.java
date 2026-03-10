package es.codeurjc.daw.library.service;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.GuideRepository;
import es.codeurjc.daw.library.repository.TourRepository;
import jakarta.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


@Component
@Order(3)
public class GuideInitializer implements CommandLineRunner {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private TourRepository tourRepository;


    private byte[] loadImage(String path) {
        try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Image not found: " + path);
            }
            return is.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void run(String... args) {
        if (guideRepository.count() != 0)
            return;

        Tour tour = tourRepository.findAll().get(0);

        createGuide("Laura", "Méndez", 199.99, tour, loadImage("static/images/guides/laura.webp"));
        createGuide("Carlos", "García", 249.99, tour, loadImage("static/images/guides/carlos.webp"));
        createGuide("María", "López", 299.99, tour, loadImage("static/images/guides/maria.webp"));
        createGuide("Javier", "Sánchez", 149.99, tour, loadImage("static/images/guides/javier.webp"));

        System.out.println(">>> Guides initialized");
    }

    private void createGuide(String name, String lastName, double price, Tour tour, byte[] profilePicture) {
        Guide g = new Guide();
        g.setName(name);
        g.setLastName(lastName);
        g.setPrice(price);
        g.setTour(tour);
        g.setProfilePicture(profilePicture); // Asegúrate de que el modelo Guide tenga este atributo

        guideRepository.save(g);
    }


    /**
     * generates default pfp
     * @param roleText first line text (user role)
     * @param nameText second line text (users name)
     * @param bgColor image bg color
     * @return b64 string ready to store in db
     */
    public @Nullable byte[] generateDefaultAvatar (String roleText, String nameText, Color bgColor) {
        // image size
        int width = 200;
        int height = 200;

        // buffered image
        BufferedImage image = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // anti-analising for smoother text
        g.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // add background
        g.setColor (bgColor);
        g.fillRect (0, 0, width, height);

        // add text
        g.setColor (Color.WHITE);
        g.setFont (new Font ("Arial", Font.BOLD, 28));
        FontMetrics metrics = g.getFontMetrics();

        // calculate center for role and name
        // role
        int x1 = (width - metrics.stringWidth(roleText)) / 2;
        int y1 = (height / 2) - 15;
        g.drawString (roleText, x1, y1);
        // name
        int x2 = (width - metrics.stringWidth(nameText)) / 2;
        int y2 = (height / 2) + 25;
        g.drawString (nameText, x2, y2);
        g.dispose();

        // store image
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write (image, "png", baos);
            return baos.toByteArray(); //return raw bytes
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

