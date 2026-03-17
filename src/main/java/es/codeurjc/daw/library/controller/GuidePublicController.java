package es.codeurjc.daw.library.controller;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.service.GuideService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GuidePublicController {

    private final GuideService guideService;

    public GuidePublicController(GuideService guideService) {
        this.guideService = guideService;
    }

    @GetMapping("/guides/{id}/image")
    @ResponseBody
    public ResponseEntity<byte[]> getGuideImage(@PathVariable Long id) {

        Guide guide = guideService.findById(id);

        if (guide == null || guide.getProfilePicture() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(guide.getProfilePicture());
    }
}