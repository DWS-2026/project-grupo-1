package es.apexexpeditions.library.controller;

import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.FileService;
import es.apexexpeditions.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
@RequestMapping("/profile/certificate")
public class CertificateWebController {

    @Autowired private FileService fileService;
    @Autowired private UserService userService;

    @PostMapping("/upload")
    public String uploadWeb(@RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.getLoggedUser();
        if (user != null && !userService.isAdmin(user) && !file.isEmpty()) {
            String name = fileService.savePdf(file, user.getId());
            user.setMedicalCertificateName(name);
            userService.save(user);
        }
        return "redirect:/profile";
    }

    @GetMapping("/view")
    public ResponseEntity<byte[]> viewWeb() {
        User user = userService.getLoggedUser();
        if (user == null || user.getMedicalCertificateName() == null) return ResponseEntity.notFound().build();
        
        try {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(fileService.getPdf(user.getMedicalCertificateName()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}