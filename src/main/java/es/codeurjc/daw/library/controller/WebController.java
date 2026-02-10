package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "user/index";
    }

    @GetMapping("/packages")
    public String packages() {
        return "user/packages";
    }

    @GetMapping("/guides")
    public String guides() {
        return "user/guides";
    }

    @GetMapping("/services")
    public String services() {
        return "user/services";
    }
    @GetMapping("/about")
    public String about() {
        return "user/about";
    }
    @GetMapping("/contact")
    public String contact() {
        return "user/contact";
    }
    @GetMapping("/carrito")
    public String carrito() {
        return "user/carrito";
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



}
