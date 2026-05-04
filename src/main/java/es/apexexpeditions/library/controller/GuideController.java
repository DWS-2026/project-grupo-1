package es.apexexpeditions.library.controller;

import es.apexexpeditions.library.model.Guide;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.service.GuideService;
import es.apexexpeditions.library.service.TourService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
/*import org.springframework.http.ResponseEntity;*/
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import es.apexexpeditions.library.service.NotificationService;

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
            @RequestParam(required = false) Boolean enabled,
            RedirectAttributes redirectAttributes
    ) {

        try {
            // 3. FIX BUFFER OVERFLOW: Comprobar el tamaño (en tu DTO y BD le pusiste max 100)
            if (name == null || name.length() > 100 || lastName == null || lastName.length() > 100) {
                redirectAttributes.addFlashAttribute("error", "El nombre o apellido exceden el tamaño permitido (100 caracteres).");
                return "redirect:/admin/guides/edit/" + id;
            }

            String safeName = name.replaceAll("[%!]", "");
            String safeLastName = lastName.replaceAll("[%!]", "");    

            Guide guide = guideService.findById(id);    

            guide.setName(safeName);
            guide.setLastName(safeLastName);
            guide.setPrice(price);

            Tour tour = tourService.findById(tourId);
            guide.setTour(tour);

            guide.setEnabled(enabled != null);

            if (imageFile != null && !imageFile.isEmpty()) {
            guide.setProfilePicture(imageFile.getBytes());
            }

            guideService.save(guide);
            redirectAttributes.addFlashAttribute("success", "Guía actualizado correctamente.");
            return "redirect:/admin/guides";

        } catch (Exception e) {
            // 5. FIX ERROR 500: Si algo explota, lo capturamos aquí
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al guardar los cambios.");
            return "redirect:/admin/guides/edit/" + id;
        
        }

            
    }


    @GetMapping("/add")
    public String showAddGuideForm(Model model) {
        model.addAttribute("tours", tourService.findAll());
        return "admin/guide-add";
    }



    @GetMapping("/guides/{id}/image")
        @ResponseBody
        public ResponseEntity<byte[]> getGuideImage(@PathVariable Long id) {

            Guide guide = guideService.findById(id);

            if (guide == null || guide.getProfilePicture() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/webp")
                    .body(guide.getProfilePicture());
        }

    
    @GetMapping("/guides")
        public String showGuides(Model model) {

            List<Guide> guides = guideService.findAll();

            if (guides.size() > 6) {
                guides = guides.subList(0, 6); // 6 max
            }

            model.addAttribute("guides", guides);

            return "guides";
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
                            @RequestParam(required = false) MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {

        try {
            // 1. FIX BUFFER OVERFLOW: Validar longitudes antes de instanciar nada
            if (name == null || name.length() > 100 || lastName == null || lastName.length() > 150) {
                redirectAttributes.addFlashAttribute("error", "El nombre o apellido exceden el tamaño permitido.");
                return "redirect:/admin/guides/add"; // Redirige de vuelta al formulario
            }

            // 2. FIX FORMAT STRING ERROR: Limpiar caracteres especiales de formato que inyectó ZAP
            // Evita que símbolos como %s o %n rompan logs o funciones internas
            String safeName = name.replaceAll("[%!]", "");
            String safeLastName = lastName.replaceAll("[%!]", "");

            Guide newGuide = new Guide();
            newGuide.setName(safeName);
            newGuide.setLastName(safeLastName);
            newGuide.setPrice(price);

            // Tour obligatorio
            Tour tour = tourService.findById(tourId);
            newGuide.setTour(tour);

            // Imagen opcional
            if (imageFile != null && !imageFile.isEmpty()) {
                newGuide.setProfilePicture(imageFile.getBytes());
            } else {
                byte[] avatar = guideService.generateDefaultAvatar("Guía", safeName, new Color(40,167,69));
                newGuide.setProfilePicture(avatar);
            }

            guideService.save(newGuide);    
            
            // Cuidado aquí: Si notificationService usa String.format() internamente, 
            // pasarle variables sin limpiar causa Format String Errors.
            notificationService.notify("Guía creado: " + safeName, "fas fa-user", "bg-success");

            redirectAttributes.addFlashAttribute("success", "Guía creado correctamente.");
            return "redirect:/admin/guides";

        } catch (Exception e) {
            // 3. FIX APPLICATION ERROR DISCLOSURE (Error 500)
            // Capturamos cualquier error (incluyendo IOException del imageFile.getBytes() 
            // o fallos de base de datos) para que ZAP/el usuario no vean una página de error 500 con código interno.
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al procesar la solicitud.");
            return "redirect:/admin/guides/add";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteGuide(@PathVariable Long id) {
        guideService.deleteById(id);
        return "redirect:/admin/guides";
    } 
}
