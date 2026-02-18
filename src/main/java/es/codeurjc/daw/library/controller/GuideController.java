package es.codeurjc.daw.library.controller;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.service.GuideService;
import es.codeurjc.daw.library.service.TourService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/guides")
public class GuideController {

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
    public String saveGuide(@RequestParam Long id, @RequestParam String name, @RequestParam String lastName, @RequestParam double price, @RequestParam Long tourId) {
        Guide guide = guideService.findById(id);
        guide.setName(name);
        guide.setLastName(lastName);
        guide.setPrice(price);
        Tour tour = tourService.findById(tourId);
        guide.setTour(tour);
        guideService.save(guide);
        return "redirect:/admin/guides";
    }
}
