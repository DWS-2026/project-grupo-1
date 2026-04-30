package es.codeurjc.daw.library.controller.rest;

import es.codeurjc.daw.library.dto.ImageDTO;
import es.codeurjc.daw.library.dto.ImageMapper;
import es.codeurjc.daw.library.dto.TourResponseDTO;
import es.codeurjc.daw.library.dto.TourMapper;
import es.codeurjc.daw.library.dto.TourRequestDTO;
import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.service.ImageService;
import es.codeurjc.daw.library.service.TourService;
import es.codeurjc.daw.library.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/tours")
public class TourRestController {

    @Autowired
    private TourService tourService;

    @Autowired
    private TourMapper tourMapper;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private UserService userService;

    // =========================================================
    // CRUD TOURS
    // =========================================================

    @GetMapping("")
    public ResponseEntity<Page<TourResponseDTO>> getAllTours(@PageableDefault(size = 10) Pageable pageable) {

        User user = userService.getLoggedUser();
        Page<Tour> toursPage;

        // Broken Access Control
        if (user != null && userService.isAdmin(user)) {
            toursPage = tourService.findAll(pageable);
        } else {
            toursPage = tourService.findByHiddenFalse(pageable);
        }

        Page<TourResponseDTO> dtoPage = toursPage.map(tourMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponseDTO> getTour(@PathVariable long id) {

        Tour tour = tourService.findById(id);
        User user = userService.getLoggedUser();

        // Broken Access Control
        if (tour.isHidden() && (user == null || !userService.isAdmin(user))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(tourMapper.toDTO(tour));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TourResponseDTO> createTour(
            @ModelAttribute TourRequestDTO tourDTO, @ModelAttribute MultipartFile imageFile) throws Exception {

        User user = userService.getLoggedUser();

        // Broken Access Control
        if (user == null || !userService.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Tour saved = tourService.createTour(tourDTO, imageFile);

        URI location = fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(tourMapper.toDTO(saved));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TourResponseDTO> replaceTour(
            @PathVariable long id,
            @ModelAttribute TourRequestDTO tourDTO,
            @RequestParam MultipartFile imageFile) throws Exception {

        if (!tourService.existsById(id)) {
            throw new NoSuchElementException("Tour not found");
        }

        // Broken Access Control
        User user = userService.getLoggedUser();

        if (user == null || !userService.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Tour updated = tourService.replaceTour(id, tourDTO, imageFile);

        return ResponseEntity.ok(tourMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TourResponseDTO> deleteTour(@PathVariable long id) {

        User user = userService.getLoggedUser();

        // Broken Access Control
        if (user == null || !userService.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Tour tour = tourService.deleteById(id);
        return ResponseEntity.ok(tourMapper.toDTO(tour));
    }

    // =========================================================
    // TOUR IMAGE
    // =========================================================

    @GetMapping("/{id}/image")
    public ResponseEntity<ImageDTO> getTourImage(@PathVariable long id) {
        Tour tour = tourService.findById(id);
        Image image = tour.getTourImage();
        User user = userService.getLoggedUser();

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        // Broken Access Control
        if ((user == null || !userService.isAdmin(user)) && tour.isHidden()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(imageMapper.toDTO(image));
    }

    @GetMapping("/{id}/image/media")
    public ResponseEntity<byte[]> getTourImageFile(@PathVariable long id) {

        Tour tour = tourService.findById(id);
        User user = userService.getLoggedUser();
        Image image = tour.getTourImage();

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        // Broken Access Control
        if ((user == null || !userService.isAdmin(user)) && tour.isHidden()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        byte[] imageBytes = imageService.getImageFile(image.getId());

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(imageBytes);
    }

    @PutMapping("/{id}/image/media")
    public ResponseEntity<ImageDTO> uploadTourImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {

        // Broken Access Control
        User user = userService.getLoggedUser();

        if (user == null || !userService.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Tour tour = tourService.findById(id);

        if (tour.getTourImage() != null) {
            imageService.replaceImageFile(tour.getTourImage().getId(), imageFile);
        } else {
            tourService.setTourImage(id, imageFile);
        }

        URI location = fromCurrentRequest()
                .path("/media")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(imageMapper.toDTO(tourService.findById(id).getTourImage()));
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<ImageDTO> deleteTourImage(@PathVariable long id) {

        // Broken Access Control
        User user = userService.getLoggedUser();

        if (user == null || !userService.isAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Tour tour = tourService.findById(id);
        Image image = tour.getTourImage();

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        tourService.removeTourImage(id);
        imageService.deleteImage(image.getId());

        return ResponseEntity.ok(imageMapper.toDTO(image));
    }
}