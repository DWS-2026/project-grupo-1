package es.codeurjc.daw.library.controller;

import java.sql.Blob;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.TourRepository;
import es.codeurjc.daw.library.service.TourService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourService tourService;

    @GetMapping({ "", "/index" })
    public String adminHome() {
        return "admin/admin-index";
    }

    @GetMapping("/buttons")
    public String buttons() {
        return "admin/buttons";
    }

    @GetMapping("/cards")
    public String cards() {
        return "admin/cards";
    }

    @GetMapping("/charts")
    public String charts() {
        return "admin/charts";
    }

    @GetMapping("/blank")
    public String blank() {
        return "admin/blank";
    }

    @GetMapping("/404")
    public String pagina404() {
        return "admin/404";
    }

    @GetMapping("/profile")
    public String profile() {
        return "admin/profile";
    }

    @GetMapping("/tours")
    public String tours(Model model) {
        List<Tour> tours = tourRepository.findAll();
        model.addAttribute("tours", tours);
        return "admin/tours";
    }

    @GetMapping("/tour-add")
    public String addTour() {
        return "admin/tour-add";
    }

    @PostMapping("/tour-add")
    public String addTour(@ModelAttribute Tour tour,
            @RequestParam(required = false) MultipartFile imageFile) {

        try {
            tourService.save(tour, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/tours";
    }

    @GetMapping("/tour-edit/{id}")
    public String tourEdit(@PathVariable Long id, Model model,
            HttpServletRequest request) {

        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour no encontrado: " + id));

        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");

        model.addAttribute("tour", tour);
        model.addAttribute("_csrf", csrf);

        return "admin/tour-edit";
    }

    @PostMapping("/tour-edit/{id}")
    public String updateTour(@PathVariable Long id, @ModelAttribute Tour tourData,
            @RequestParam(required = false) MultipartFile imageFile) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour no encontrado: " + id));

        // Actualizamos campos
        tour.setName(tourData.getName());
        tour.setPrice(tourData.getPrice());
        tour.setDescription(tourData.getDescription());
        tour.setDuration(tourData.getDuration());
        tour.setNumPeople(tourData.getNumPeople());
        tour.setHotelIncluded(tourData.isHotelIncluded());

        try {
            tourService.save(tour, imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/tours";
    }

    @GetMapping("/reviews")
    public String reviews() {
        return "admin/reviews";
    }

    @GetMapping("/review-edit")
    public String review_edit() {
        return "admin/review-edit";
    }

    @GetMapping("/utilities-animation")
    public String utilities_animation() {
        return "admin/utilities-animation";
    }

    @GetMapping("/utilities-border")
    public String utilities_border() {
        return "admin/utilities-border";
    }

    @GetMapping("/utilities-color")
    public String utilities_color() {
        return "admin/utilities-color";
    }

    @GetMapping("/utilities-other")
    public String utilities_other() {
        return "admin/utilities-other";
    }

}