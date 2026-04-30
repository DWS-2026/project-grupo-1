package es.apexexpeditions.library.controller;






// region =========== imports =================
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.model.Review;
import es.apexexpeditions.library.service.TourService;
import es.apexexpeditions.library.service.ReviewService;
// endregion






@Controller
@RequestMapping("/admin")
public class AdminWebController {
    // region =========== attributes =================
    private final ReviewService reviewService;
    // endregion





    // region =========== autowired =================
    @Autowired
    private TourService tourService;
    // endregion





    // region =========== constructor =================
    AdminWebController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    // endregion





    // region =========== GetMapping =================
    // region 1. "/index"
    @GetMapping({ "", "/index" })
    public String adminHome() {
        return "admin/admin-index";
    }
    // endregion



    // region 2. "/buttons"
    @GetMapping("/buttons")
    public String buttons() {
        return "admin/buttons";
    }
    // endregion



    // region 3. "/cards"
    @GetMapping("/cards")
    public String cards() {
        return "admin/cards";
    }
    // endregion



    // region 4. "/charts"
    @GetMapping("/charts")
    public String charts() {
        return "admin/charts";
    }
    // endregion



    // region 5. "/blank"
    @GetMapping("/blank")
    public String blank() {
        return "admin/blank";
    }
    // endregion



    // region 6. "/404"
    @GetMapping("/404")
    public String page404() {
        return "admin/404";
    }
    // endregion



    // region 7. "/profile"
    @GetMapping("/profile")
    public String profile() {
        return "admin/profile";
    }
    // endregion


    // region 8. "/tours"
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
        model.addAttribute("size", pageable.getPageSize());
        return "admin/tours";
    }
    // endregion



    // region 9. "/tour-add"
    @GetMapping("/tour-add")
    public String addTour() {
        return "admin/tour-add";
    }
    // endregion



    // region 10. "/tour-edit/{id}"
    @GetMapping("/tour-edit/{id}")
    public String tourEdit(@PathVariable Long id, Model model,
                           HttpServletRequest request) {

        Tour tour = tourService.findById(id);

        model.addAttribute("tour", tour);

        return "admin/tour-edit";
    }
    // endregion



    // region 11. "/reviews"
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
    // endregion



    // region 12. /"utilities-animation"
    @GetMapping("/utilities-animation")
    public String utilities_animation() {
        return "admin/utilities-animation";
    }
    // endregion



    // region 13. /"utilities-border"
    @GetMapping("/utilities-border")
    public String utilities_border() {
        return "admin/utilities-border";
    }
    //endregion



    // region 14. /"utilities-color"
    @GetMapping("/utilities-color")
    public String utilities_color() {
        return "admin/utilities-color";
    }
    // endregion



    // region 15. /"utilities-other"
    @GetMapping("/utilities-other")
    public String utilities_other() {
        return "admin/utilities-other";
    }
    // endregion


    // region 16. /"/reviews/tour/{tourId}"
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
    // endregion




    // region =========== PostMapping =================
    // region 1. "tour-hide/{id}"
    @PostMapping("tour-hide/{id}")
    public String changeTourVisibility(@PathVariable Long id) {

        Tour tour = tourService.findById(id);

        if (tour != null) {
            tour.setHidden(!tour.isHidden());
            tourService.save(tour);
        }

        return "redirect:/admin/tours";
    }
    // endregion



    // region 2. "tour-delete/{id}"
    @PostMapping("/tour-delete/{id}")
    public String deleteTour(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        if (tourService.findById(id) != null) {
            tourService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tour eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "El tour no existe.");
        }

        return "redirect:/admin/tours";
    }
    // endregion



    // region 3. "/tour-add"
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
    // endregion



    // region 4. "/tour-edit/{id}"
    @PostMapping("/tour-edit/{id}")
    public String updateTour(@PathVariable Long id, @ModelAttribute Tour tourData,
            @RequestParam(required = false) MultipartFile imageFile) {
        Tour tour = tourService.findById(id);

        if (tour == null) {
            return "redirect:/admin/tours";
        }

        // Update fields
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
    // endregion



    // region 5. "/review-hide/{id}"
    @PostMapping("/review-hide/{id}")
    public String occultReview(@PathVariable Long id, @RequestParam boolean hide) {

        reviewService.findById(id).ifPresent(review -> {
            review.setHidden(hide);
            reviewService.save(review);
        });

        return "redirect:/admin/reviews";
    }
    // endregion



    // region 6. "/review-delete/{id}"
    @PostMapping("/review-delete/{id}")
    public String deleteReview(@PathVariable Long id) {

        reviewService.findById(id).ifPresent(review -> {
            reviewService.deleteById(id);
        });

        return "redirect:/admin/reviews";
    }
    // endregion
    // endregion
}