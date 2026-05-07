package es.apexexpeditions.library.controller.rest;

import es.apexexpeditions.library.dto.image.ImageDTO;
import es.apexexpeditions.library.dto.image.ImageMapper;
import es.apexexpeditions.library.dto.tour.TourMapper;
import es.apexexpeditions.library.dto.tour.TourRequestDTO;
import es.apexexpeditions.library.dto.tour.TourResponseDTO;
import es.apexexpeditions.library.dto.tour.TourStatsDTO;
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.ImageService;
import es.apexexpeditions.library.service.TourService;
import es.apexexpeditions.library.service.UserService;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.net.URI;
import java.util.NoSuchElementException;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;






/**
 * API REST v1: tour catalog management
 * * --- SECURITY AND ACCESS STRUCTURE ---
 * - ADMIN: full access to create, modify, delete tours, view hidden tours, and access statistics
 * - PUBLIC: view visible tours and their images
 *
 * --- TOUR ENDPOINTS ---
 * - GET    /api/v1/tours                  : paginated list of tours (visible for public, all for admin)
 * - GET    /api/v1/tours/{id}             : details of a specific tour. req: admin if hidden
 * - POST   /api/v1/tours                  : creates a new tour with an image. req: admin
 * - PUT    /api/v1/tours/{id}             : replaces a tour's data and image. req: admin
 * - DELETE /api/v1/tours/{id}             : removes a tour from the system
 *
 * --- TOUR IMAGE ENDPOINTS ---
 * - GET    /api/v1/tours/{id}/image       : retrieves the tour's image metadata
 * - GET    /api/v1/tours/{id}/image/media : downloads the binary jpeg file of the tour
 * - PUT    /api/v1/tours/{id}/image/media : uploads or replaces the tour's image
 * - DELETE /api/v1/tours/{id}/image       : unlinks and deletes the tour's image
 *
 * --- STATISTICS ENDPOINTS ---
 * - GET    /api/v1/tours/stats            : returns tour global statistics. req: admin
 */
@RestController
@RequestMapping("/api/v1/tours")
@Tag(name = "Tours", description = "Gestión del catálogo de expediciones")
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

    @Operation(summary = "Obtener catálogo de tours", description = "Devuelve una página de tours. Los clientes ven solo los tours visibles, los administradores los ven todos.")
    @ApiResponse(responseCode = "200", description = "Catálogo obtenido exitosamente")
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

    @Operation(summary = "Obtener un tour por ID", description = "Obtiene los detalles de un tour. Si es oculto, requiere permisos ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tour encontrado", content = @Content(schema = @Schema(implementation = TourResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado a un tour oculto", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
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

    @Operation(summary = "Crear expedición", description = "Crea un nuevo tour en base de datos. Exclusivo para administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tour creado", content = @Content(schema = @Schema(implementation = TourResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Falta imagen o datos malformados", content = @Content),
        @ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TourResponseDTO> createTour(
            @ModelAttribute TourRequestDTO tourDTO, @ModelAttribute MultipartFile imageFile) throws Exception {

        if (imageFile == null || imageFile.isEmpty()) {
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

    @Operation(summary = "Reemplazar expedición", description = "Actualiza todos los datos de un tour, incluida la foto. (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente", content = @Content(schema = @Schema(implementation = TourResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content),
        @ApiResponse(responseCode = "404", description = "Tour no encontrado", content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TourResponseDTO> replaceTour(
            @PathVariable long id,
            @ModelAttribute TourRequestDTO tourDTO,
            @RequestParam(required = false) MultipartFile imageFile) throws Exception {

        if (!tourService.existsById(id)) {
            throw new NoSuchElementException("Tour not found");
        }

        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Tour updated = tourService.replaceTour(id, tourDTO, imageFile);

        return ResponseEntity.ok(tourMapper.toDTO(updated));
    }

    @Operation(summary = "Eliminar tour", description = "Borra físicamente una expedición. (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tour eliminado", content = @Content(schema = @Schema(implementation = TourResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<TourResponseDTO> deleteTour(@PathVariable long id) {

        Tour tour = tourService.deleteById(id);
        return ResponseEntity.ok(tourMapper.toDTO(tour));
    }

    // =========================================================
    // TOUR IMAGE
    // =========================================================

    @Operation(summary = "Datos imagen del tour", description = "Obtiene los metadatos de la imagen asignada al tour.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen encontrada", content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "403", description = "Tour oculto (acceso denegado)", content = @Content),
        @ApiResponse(responseCode = "404", description = "No tiene imagen", content = @Content)
    })
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

    @Operation(summary = "Descargar binario de la foto del tour", description = "Devuelve los bytes JPEG para renderizar la cabecera del tour.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo recuperado", content = @Content(mediaType = "image/jpeg")),
        @ApiResponse(responseCode = "403", description = "Acceso denegado a archivo privado", content = @Content),
        @ApiResponse(responseCode = "404", description = "No hay archivo vinculado", content = @Content)
    })
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

    @Operation(summary = "Actualizar/Subir foto del tour", description = "Reemplaza el archivo JPEG del tour (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen vinculada", content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "El archivo enviado está vacío", content = @Content),
        @ApiResponse(responseCode = "403", description = "Requiere permisos ADMIN", content = @Content)
    })
    @PutMapping("/{id}/image/media")
    public ResponseEntity<ImageDTO> uploadTourImage(
            @PathVariable long id,
            @RequestParam MultipartFile imageFile) throws IOException {

        if (imageFile == null || imageFile.isEmpty()) {
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

    @Operation(summary = "Desvincular y eliminar imagen", description = "Quita la foto del tour y elimina el recurso físico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Borrada con éxito", content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "El tour no tiene imagen", content = @Content)
    })
    @DeleteMapping("/{id}/image")
    public ResponseEntity<ImageDTO> deleteTourImage(@PathVariable long id) {

        Tour tour = tourService.findById(id);
        Image image = tour.getTourImage();

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        tourService.removeTourImage(id);
        imageService.deleteImage(image.getId());

        return ResponseEntity.ok(imageMapper.toDTO(image));
    }

    @GetMapping("/stats")
    public ResponseEntity<TourStatsDTO> getTourStats() {
        User loggedUser = userService.getLoggedUser();

        if (loggedUser == null || !userService.isAdmin(loggedUser)) {   // check auth
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(tourService.getTourStats());
    }
}