package es.apexexpeditions.library.controller.rest;


import es.apexexpeditions.library.dto.image.ImageDTO;
import es.apexexpeditions.library.dto.image.ImageMapper;
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.service.ImageService;

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
// endregion



@RestController
@RequestMapping ("/api/v1/images")
public class ImageRestController {
    // region =========== autowired =================
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageMapper imageMapper;
    // endregion


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
        Image image = imageService.getImage(id);

        if (image.getImageFile() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(image.getImageFile());
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