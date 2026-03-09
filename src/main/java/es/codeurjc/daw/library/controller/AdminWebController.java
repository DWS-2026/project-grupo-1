package es.codeurjc.daw.library.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.Review;
import es.codeurjc.daw.library.service.TourService;
import es.codeurjc.daw.library.service.ReviewService;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final ReviewService reviewService;

    @Autowired
    private TourService tourService;

    AdminWebController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

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
    public String getTours(Model model, @PageableDefault(size = 5) Pageable pageable) {

        Page<Tour> page = tourService.findAll(pageable);

        model.addAttribute("tours", page.getContent());

        model.addAttribute("hasPrev", page.hasPrevious());
        model.addAttribute("hasNext", page.hasNext());
        model.addAttribute("prev", page.getNumber() - 1);
        model.addAttribute("next", page.getNumber() + 1);
        model.addAttribute("currentPage", page.getNumber() + 1);
        model.addAttribute("totalPages", page.getTotalPages());

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

        Tour tour = tourService.findById(id);

        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");

        model.addAttribute("tour", tour);
        model.addAttribute("_csrf", csrf);

        return "admin/tour-edit";
    }

    @PostMapping("/tour-edit/{id}")
    public String updateTour(@PathVariable Long id, @ModelAttribute Tour tourData,
            @RequestParam(required = false) MultipartFile imageFile) {
        Tour tour = tourService.findById(id);

        if (tour == null) {
            return "redirect:/admin/tours";
        }

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
    public String allReviews(@RequestParam(required = false) Long tourId, Model model) {

        List<Review> reviews;

        if (tourId != null) {
            reviews = reviewService.findByTourId(tourId);
        } else {
            reviews = reviewService.findAll();
        }

        model.addAttribute("reviews", reviews);

        return "admin/reviews";
    }

    @PostMapping("/review-hide/{id}")
    public String occultReview(@PathVariable Long id, @RequestParam boolean hide) {

        reviewService.findById(id).ifPresent(review -> {
            review.setHidden(hide);
            reviewService.save(review);
        });

        return "redirect:/admin/reviews";
    }

    @PostMapping("/review-delete/{id}")
    public String deleteReview(@PathVariable Long id) {

        reviewService.findById(id).ifPresent(review -> {
            reviewService.deleteById(id);
        });

        return "redirect:/admin/reviews";
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

    @GetMapping("/reviews/tour/{tourId}")
    public String showReviews(@PathVariable Long tourId, Model model) {

        Tour tour = tourService.findById(tourId);

        if (tour == null) {
            return "redirect:/admin/tours";
        }

        model.addAttribute("tour", tour);
        model.addAttribute("reviews", tour.getReviews());

        return "admin/tour-review";
    }

}