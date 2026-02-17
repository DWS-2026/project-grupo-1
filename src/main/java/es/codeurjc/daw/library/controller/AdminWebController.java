package es.codeurjc.daw.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.TourRepository;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @Autowired
    private TourRepository tourRepository;

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

    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }

    @GetMapping("/user-edit")
    public String user_edit() {
        return "admin/user-edit";
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

    @GetMapping("/add-tour")
    public String addTour() {
        return "admin/add-tour";
    }

    @GetMapping("/tour-edit/{id}")
    public String tourEdit(@PathVariable Long id, Model model) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour no encontrado: " + id));
        model.addAttribute("tour", tour);
        return "admin/tour-edit";
    }

    @PostMapping("/tour-edit/{id}")
    public String updateTour(@PathVariable Long id, @ModelAttribute Tour tourData) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tour no encontrado: " + id));

        tour.setName(tourData.getName());
        tour.setPrice(tourData.getPrice());
        tour.setDescription(tourData.getDescription());
        // Aquí puedes agregar manejo de imagen si quieres guardar el archivo

        tourRepository.save(tour);
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

    @GetMapping("/guides")
    public String guides() {
        return "admin/guides";
    }

    @GetMapping("/guides-edit")
    public String guides_edit() {
        return "admin/guides-edit";
    }

}