package es.apexexpeditions.library.controller.rest;

import es.apexexpeditions.library.dto.GuideRequestDTO;
import es.apexexpeditions.library.dto.GuideResponseDTO;
import es.apexexpeditions.library.service.GuideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/guides")
public class GuideRestController {

    @Autowired
    private GuideService guideService;

    @GetMapping
    public ResponseEntity<Page<GuideResponseDTO>> getGuides(Pageable pageable) {
        return ResponseEntity.ok(guideService.findAllDTOs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> getGuide(@PathVariable Long id) {
        return ResponseEntity.ok(guideService.findDTOById(id));
    }

    @PostMapping
    public ResponseEntity<GuideResponseDTO> createGuide(@Valid @RequestBody GuideRequestDTO request) {
        GuideResponseDTO savedGuide = guideService.create(request);

        URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedGuide.id())
                .toUri();

        return ResponseEntity.created(location).body(savedGuide);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> replaceGuide(@PathVariable Long id, 
                                                         @Valid @RequestBody GuideRequestDTO request) {
        return ResponseEntity.ok(guideService.replace(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long id) {
        guideService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}