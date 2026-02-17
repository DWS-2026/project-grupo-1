package es.codeurjc.daw.library.controller;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.service.GuideService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/guides")
public class GuideController {

    private final GuideService guideService;

    public GuideController(GuideService guideService) {
        this.guideService = guideService;
    }

    // Mostrar formulario de edición
    @GetMapping("/edit/{id}")
    public String editGuide(@PathVariable Long id, Model model) {
        Guide guide = guideService.findById(id);
        model.addAttribute("guide", guide);
        return "edit-guide"; // nombre del HTML
    }

    // Guardar cambios
    @PostMapping("/edit/{id}")
    public String updateGuide(@PathVariable Long id, @ModelAttribute Guide guide) {
        guide.setId(id);
        guideService.save(guide);

        return "redirect:/guides";
    }
}
