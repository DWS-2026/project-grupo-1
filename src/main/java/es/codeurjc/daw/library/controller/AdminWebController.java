package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @GetMapping({"", "/index"})
    public String adminHome() {
        return "admin/admin_index";
    }

    @GetMapping("/buttons")
    public String buttons() {
        return "admin/buttons";
    }

    // Tarjetas
    @GetMapping("/cards")
    public String cards() {
        return "admin/cards";
    }

    // Estadísticas
    @GetMapping("/charts")
    public String charts() {
        return "admin/charts";
    }

    // Página en blanco
    @GetMapping("/blank")
    public String blank() {
        return "admin/blank";
    }

    // Usuarios
    @GetMapping("/users")
    public String users() {
        return "admin/users";
    }

    // Tours
    @GetMapping("/tours")
    public String tours() {
        return "admin/tours";
    }

    // Reviews
    @GetMapping("/reviews")
    public String reviews() {
        return "admin/reviews";
    }
}