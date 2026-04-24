package es.codeurjc.daw.library.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.daw.library.dto.TourDTO;
import es.codeurjc.daw.library.dto.TourMapper;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.service.TourService;

@RestController
@RequestMapping("/api/v1/tours")
public class TourRestController {

    @Autowired
    private TourService tourService;

    
    @Autowired
    private TourMapper tourMapper;

    @GetMapping
    public ResponseEntity<List<TourDTO>> listarTours() {
        List<TourDTO> toursDTO = tourMapper.toDTOs(tourService.findAll());
        return ResponseEntity.ok(toursDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> obtenerTour(@PathVariable Long id) {
        Tour tour = tourService.findById(id);
        if (tour == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tourMapper.toDTO(tour));
    }
}