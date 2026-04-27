package es.codeurjc.daw.library.service;

// region =========== imports =================
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired; // to inject repository
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service; // to define service
import es.codeurjc.daw.library.model.User; // to work with users
import es.codeurjc.daw.library.repository.UserRepository; // to search by email

import java.util.List; // for user lists
import javax.imageio.ImageIO; // for pfp generation
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// for dto
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.codeurjc.daw.library.dto.UserResponseDTO;
// endregion

/**
 * service layer class that handles business logic related to users
 * acts as intermediary between WebController and UserRepository
 */
@Service
public class UserService {
    // region =========== autowired =================
    @Autowired // repository injection for db access
    private UserRepository userRepository;
    // endregion

    // region =========== derived query methods =================
    // search user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // store user object into db
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    // deletes user from db
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    // retrieve all users from db
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // find user by db id
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // check if email is registered in db already
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // check if a phone is already in use (as primary or secondary and by any user)
    public boolean phoneExists(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByMainPhone(phone) || userRepository.existsBySecondaryPhone(phone);
    }

    // for dto
    // retrieve all users from db in pageable
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    // endregion

    // region =========== method: generateDefaultAvatar =================
    /**
     * generates default pfp
     * 
     * @param roleText first line text (user role)
     * @param nameText second line text (users name)
     * @param bgColor  image bg color
     * @return b64 string ready to store in db
     */
    public @Nullable byte[] generateDefaultAvatar(String roleText, String nameText, Color bgColor) {
        // image size
        int width = 200;
        int height = 200;

        // buffered image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // anti-analising for smoother text
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // add background
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);

        // add text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics metrics = g.getFontMetrics();

        // calculate center for role and name
        // role
        int x1 = (width - metrics.stringWidth(roleText)) / 2;
        int y1 = (height / 2) - 15;
        g.drawString(roleText, x1, y1);
        // name
        int x2 = (width - metrics.stringWidth(nameText)) / 2;
        int y2 = (height / 2) + 25;
        g.drawString(nameText, x2, y2);
        g.dispose();

        // store image
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray(); // return raw bytes
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            return userRepository.findByEmail(email);
        }

        return null;
    }

    public boolean isAdmin(User user) {
        return user != null && user.getRoles().contains("ADMIN");
    }

    // endregion
}