package es.codeurjc.daw.library.controller;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.service.GuideService;
import es.codeurjc.daw.library.service.TourService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import es.codeurjc.daw.library.service.NotificationService;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import jakarta.annotation.Nullable;

@Controller
@RequestMapping("/admin/guides")
public class GuideController {


    @Autowired // to generate notifications
    private NotificationService notificationService;

    private final GuideService guideService;
    private final TourService tourService;

    public GuideController(GuideService guideService, TourService tourService) {
        this.guideService = guideService;
        this.tourService = tourService;
    }

    @GetMapping
    public String listGuides(Model model) {
        model.addAttribute("guides", guideService.findAll());
        return "admin/guides";
    }

    @GetMapping("/edit/{id}")
    public String editGuide(@PathVariable Long id, Model model) {
        Guide guide = guideService.findById(id);
        if (guide == null) {
            return "redirect:/admin/guides";
        }
        List<Tour> tours = tourService.findAll();
        Long currentTourId = null;
        if (guide.getTour() != null) {
            currentTourId = guide.getTour().getId();
        }
        for (Tour tour : tours) {
            tour.setSelected(currentTourId != null && tour.getId().equals(currentTourId));
        }
        model.addAttribute("guide", guide);
        model.addAttribute("tours", tours);
        return "admin/guides-edit";
    }

    @PostMapping("/save")
    public String saveGuide(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam double price,
            @RequestParam Long tourId,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) Boolean enabled
    ) throws IOException {

        Guide guide = guideService.findById(id);    

        guide.setName(name);
        guide.setLastName(lastName);
        guide.setPrice(price);

        Tour tour = tourService.findById(tourId);
        guide.setTour(tour);

        guide.setEnabled(enabled != null);

        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image =
                    Base64.getEncoder().encodeToString(imageFile.getBytes());
            guide.setProfilePicture(base64Image);
        }

        guideService.save(guide);

        return "redirect:/admin/guides";
    }


    @GetMapping("/add")
    public String showAddGuideForm(Model model) {
        model.addAttribute("tours", tourService.findAll());
        return "admin/guide-add";
}



    /**
     * processes creation of new guide
     * ensures password is encrypted and handles default avatar generation if no image is uploaded
     */
    @PostMapping("/add")
    public String saveGuide(@RequestParam String name,
                            @RequestParam String lastName,
                            @RequestParam double price,
                            @RequestParam Long tourId,
                            @RequestParam(required = false) MultipartFile imageFile) throws IOException {

        Guide newGuide = new Guide();
        newGuide.setName(name);
        newGuide.setLastName(lastName);
        newGuide.setPrice(price);

        // Tour obligatorio
        Tour tour = tourService.findById(tourId);
        newGuide.setTour(tour);

        // Imagen opcional
        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
            newGuide.setProfilePicture(base64Image);
        } else {
            String avatar = guideService.generateDefaultAvatar("Guía", name, new Color(13,110,253));
            newGuide.setProfilePicture(avatar);
        }

        guideService.save(newGuide);

        notificationService.notify("Guía creado: " + name, "fas fa-user", "bg-success");

        return "redirect:/admin/guides";
    }

    @PostMapping("/delete/{id}")
    public String deleteGuide(@PathVariable Long id) {
        guideService.deleteById(id);
        return "redirect:/admin/guides";
}

    /**
     * generates default pfp
     * @param roleText first line text (user role)
     * @param nameText second line text (users name)
     * @param bgColor image bg color
     * @return b64 string ready to store in db
     */
    public @Nullable String generateDefaultAvatar (String roleText, String nameText, Color bgColor) {
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
        g.drawString (roleText, x1, y1);
        // name
        int x2 = (width - metrics.stringWidth(nameText)) / 2;
        int y2 = (height / 2) + 25;
        g.drawString (nameText, x2, y2);
        g.dispose();

        // convert generated image to b64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // generation error fallback
        }
    }
}
