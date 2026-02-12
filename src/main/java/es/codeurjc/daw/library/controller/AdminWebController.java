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
    public String pagina404() {return "admin/404";
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
    public String tours() {
        return "admin/tours";
    }

    @GetMapping("/tour-edit")
    public String tour_edit() {
        return "admin/tour-edit";
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
    public String utilities_animation() {return "admin/utilities-animation";}
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