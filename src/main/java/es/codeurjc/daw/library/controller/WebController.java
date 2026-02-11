package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


@Controller
public class WebController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("home", true);
        return "user/index";
    }

    @GetMapping("/packages")
    public String packages(Model model) {
        model.addAttribute("packages", true);
        return "user/packages";
    }

    @GetMapping("/guides")
    public String guides(Model model) {
        model.addAttribute("guides", true);
        return "user/guides";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", true);
        return "user/services";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("about", true);
        return "user/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("contact", true);
        return "user/contact";
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("cart", true);
        return "user/cart";
    }


    @GetMapping("/register")
    public String register(){return "user/register";
    }

    @GetMapping("/login")
    public String login(){return "user/login";
    }

    @GetMapping("/profile")
    public String profile(){return "user/profile";
    }

    @GetMapping("/tour-details")
    public String tour_details(){return "user/tour-details";
    }

    @GetMapping("/checkout")
    public String checkout(){return "user/checkout";
    }

    @GetMapping("/invoice")
    public String invoice(){return  "/user/invoice";
    }

   @GetMapping("/admin-login")
    public String admin_login(){ return "/user/admin-login";
    }

    @GetMapping("/add-review")
    public String add_review(){ return "/user/add-review";
    }

    @GetMapping("/forgot-password")
    public String forgot_password(){ return "/user/forgot-password";
    }
}

