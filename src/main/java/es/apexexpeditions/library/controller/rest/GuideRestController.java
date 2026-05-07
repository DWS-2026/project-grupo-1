package es.apexexpeditions.library.controller.rest;

import es.apexexpeditions.library.dto.guide.GuideRequestDTO;
import es.apexexpeditions.library.dto.guide.GuideResponseDTO;
import es.apexexpeditions.library.service.GuideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;






/**
 * API REST v1: guide management
 * * --- SECURITY AND ACCESS STRUCTURE ---
 * - ADMIN: full control to create, replace or delete guides
 * - PUBLIC: view guides list and details
 *
 * --- GUIDE ENDPOINTS ---
 * - GET    /api/v1/guides       : paginated list of all guides (uses: GuideResponseDTO)
 * - GET    /api/v1/guides/{id}  : details of a specific guide (uses: GuideResponseDTO)
 * - POST   /api/v1/guides       : creates a new guide (uses: GuideRequestDTO)
 * - PUT    /api/v1/guides/{id}  : replaces all data of an existing guide
 * - DELETE /api/v1/guides/{id}  : permanently deletes a guide from the system
 */
@RestController
@RequestMapping("/api/v1/guides")
@Tag(name = "Guías", description = "Gestión de los guías de expedición")
public class GuideRestController {

    @Autowired
    private GuideService guideService;

    @Operation(summary = "Obtener todas las guías paginadas")
    @ApiResponse(responseCode = "200", description = "Lista de guías recuperada")
    @GetMapping
    public ResponseEntity<Page<GuideResponseDTO>> getGuides(Pageable pageable) {
        return ResponseEntity.ok(guideService.findAllDTOs(pageable));
    }

    @Operation(summary = "Obtener una guía por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Guía encontrada",
            content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = GuideResponseDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "Guía no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> getGuide(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.findDTOById(id));
    }

    @Operation(summary = "Crear una nueva guía")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Guía creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<GuideResponseDTO> createGuide(@Valid @RequestBody GuideRequestDTO request) {
        GuideResponseDTO savedGuide = guideService.create(request);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedGuide.id())
                .toUri();

        return ResponseEntity.created(location).body(savedGuide);
    }

    @Operation(summary = "Actualizar una guía existente", 
               description = "Sustituye todos los datos de una guía identificada por su ID con la información proporcionada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Guía actualizada con éxito",
            content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = GuideResponseDTO.class)) }),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o error en la validación", 
            content = @Content),
        @ApiResponse(responseCode = "404", description = "No se encontró ninguna guía con el ID proporcionada", 
            content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> replaceGuide(@PathVariable Long id, 
                                                         @Valid @RequestBody GuideRequestDTO request) {
        return ResponseEntity.ok(guideService.replace(id, request));
    }

    @Operation(summary = "Eliminar una guía", 
               description = "Elimina de forma permanente una guía del sistema mediante su identificador único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Guía eliminada correctamente", 
            content = @Content),
        @ApiResponse(responseCode = "404", description = "La guía que se intenta eliminar no existe", 
            content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}