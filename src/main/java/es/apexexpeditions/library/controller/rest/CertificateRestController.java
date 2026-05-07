package es.apexexpeditions.library.controller.rest;

import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.FileService;
import es.apexexpeditions.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users/me/certificate")
@Tag(name = "Certificados", description = "Gestión de ficheros PDF en disco")
public class CertificateRestController {

    @Autowired private FileService fileService;
    @Autowired private UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir certificado")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.getLoggedUser();
        if (user == null || userService.isAdmin(user)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) return ResponseEntity.badRequest().body("Solo PDF");

        String name = fileService.savePdf(file, user.getId());
        user.setMedicalCertificateName(name);
        userService.save(user);
        return ResponseEntity.ok(name);
    }

    @GetMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Ver mi certificado")
    public ResponseEntity<byte[]> download() throws IOException {
        User user = userService.getLoggedUser();
        if (user == null || user.getMedicalCertificateName() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(fileService.getPdf(user.getMedicalCertificateName()));
    }
}