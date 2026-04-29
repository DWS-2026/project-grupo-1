package es.codeurjc.daw.library.controller.rest;

import es.codeurjc.daw.library.dto.ImageDTO;
import es.codeurjc.daw.library.dto.ImageMapper;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;


    @GetMapping("")
    public ResponseEntity<Page<ImageDTO>> getAllImages(@PageableDefault(size = 10) Pageable pageable) {
        Page<Image> imagesPage = imageService.getAllImages(pageable);
        return ResponseEntity.ok(imagesPage.map(imageMapper::toDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable long id) {
        Image image = imageService.getImage(id);
        return ResponseEntity.ok(imageMapper.toDTO(image));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<byte[]> getImageFile(@PathVariable long id) {

        String base64 = imageService.getImageFile(id);

        if (base64 == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(imageBytes);
    }

    @PostMapping("/")
    public ResponseEntity<ImageDTO> createImage(
            @RequestParam MultipartFile imageFile) throws IOException {

        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Image image = imageService.createImage(imageFile);

        URI location = fromCurrentContextPath()
                .path("/api/v1/images/{id}/media")
                .buildAndExpand(image.getId())
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
    }

    @PutMapping("/{id}/media")
    public ResponseEntity<Void> replaceImageFile(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {


        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        imageService.replaceImageFile(id, imageFile);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ImageDTO> deleteImage(@PathVariable long id) {


        Image image = imageService.deleteImage(id);
        return ResponseEntity.ok(imageMapper.toDTO(image));
    }
}