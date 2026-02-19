package es.codeurjc.daw.library.service;




import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// for pfp generation
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import jakarta.annotation.Nullable;





/**
 * service layer class that handles business logic related to users
 * acts as intermediary between WebController and UserRepository
 */
@Service
public class UserService {

    // repository injection for db access
    @Autowired
    private UserRepository userRepository;


    /**
     * retrieves user from db using their email address
     * @param email email used for search
     * @return user object if found, null otherwise
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    /**
     * persists or updates user in dn
     * used for profile updates and initial data creation
     */
    public void save(User user) {
        userRepository.save(user);
    }


    /**
     * deletes user from db
     * Used when the user decides to close their account.
     */
    public void delete(User user) {
        userRepository.delete(user);
    }


    /**
     * Retrieves all users from the database.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }


    /**
     * Retrieves a user by their ID.
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }



    /**
     * @param roleText first line text (user role)
     * @param nameText second line text (users name)
     * @param bgColor image bg color
     * @return b64 string ready to store in db
     */
    public @Nullable String generateDefaultAvatar(String roleText, String nameText, Color bgColor) {
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