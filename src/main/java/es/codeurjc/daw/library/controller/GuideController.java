package es.codeurjc.daw.library.controller;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.service.GuideService;
import es.codeurjc.daw.library.service.TourService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
/*import org.springframework.http.ResponseEntity;*/
import org.springframework.http.HttpHeaders;
import es.codeurjc.daw.library.service.NotificationService;

import java.io.IOException;
import java.util.List;

import java.awt.*;



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
        guide.setProfilePicture(imageFile.getBytes());
        }

        guideService.save(guide);

        return "redirect:/admin/guides";
    }


    @GetMapping("/add")
    public String showAddGuideForm(Model model) {
        model.addAttribute("tours", tourService.findAll());
        return "admin/guide-add";
    }



    @GetMapping("/{id}/image")
        @ResponseBody
        public ResponseEntity<byte[]> getGuideImage(@PathVariable Long id) {

            Guide guide = guideService.findById(id);

            if (guide.getProfilePicture() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(guide.getProfilePicture());
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
            newGuide.setProfilePicture(imageFile.getBytes());
        } else {
            byte[] avatar = guideService.generateDefaultAvatar("Guía", name, new Color(40,167,69));
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
}
