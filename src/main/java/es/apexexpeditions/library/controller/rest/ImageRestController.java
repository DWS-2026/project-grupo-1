package es.apexexpeditions.library.controller.rest;






// region =========== imports =================
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
// endregion





@RestController
@RequestMapping ("/api/v1/images")
@Tag(name = "Imágenes", description = "Servicio de almacenamiento y recuperación de archivos de imagen")
public class ImageRestController {
    // region =========== autowired =================
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageMapper imageMapper;
    // endregion




    // region =========== GetMapping =================
    // region 1. getAllImages
    @Operation(summary = "Obtener todas las imágenes", description = "Devuelve una página con los metadatos (DTOs) de todas las imágenes registradas.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping("")
    public ResponseEntity<Page<ImageDTO>> getAllImages(@PageableDefault(size = 10) Pageable pageable) {
        Page<Image> imagesPage = imageService.getAllImages(pageable);
        return ResponseEntity.ok(imagesPage.map(imageMapper::toDTO));
    }
    // endregion


    // region 2. getImage
    @Operation(summary = "Obtener datos de una imagen", description = "Devuelve los metadatos de una imagen específica por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen encontrada", content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable long id) {
        Image image = imageService.getImage(id);
        return ResponseEntity.ok(imageMapper.toDTO(image));
    }
    // endregion


    // region 3. getImageFile
    @Operation(summary = "Descargar archivo binario", description = "Devuelve directamente el archivo binario (blob) de la imagen JPEG para renderizarla.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo devuelto correctamente", content = @Content(mediaType = "image/jpeg")),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada o vacía", content = @Content)
    })
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
    // endregion
    // endregion




    // region =========== PostMapping =================
    // region 1. createImage
    @Operation(summary = "Subir una nueva imagen", description = "Permite subir un archivo de imagen al sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagen subida y registrada", content = @Content(schema = @Schema(implementation = ImageDTO.class))),
        @ApiResponse(responseCode = "400", description = "Archivo vacío o inválido", content = @Content)
    })
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
    // endregion
    //  endregion




    // region =========== PutMapping =================
    // region 1. replaceImageFile
    @Operation(summary = "Reemplazar un archivo de imagen", description = "Sustituye el contenido binario de una imagen conservando su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Archivo reemplazado correctamente"),
        @ApiResponse(responseCode = "400", description = "Archivo vacío", content = @Content),
        @ApiResponse(responseCode = "404", description = "Imagen no encontrada", content = @Content)
    })
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
    // endregion
    // endregion




    // region =========== DeleteMapping =================
    // region 1. deleteImage
    @Operation(summary = "Eliminar una imagen", description = "Borra físicamente una imagen del sistema.")
    @ApiResponse(responseCode = "200", description = "Imagen eliminada", content = @Content(schema = @Schema(implementation = ImageDTO.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<ImageDTO> deleteImage(@PathVariable long id) {


        Image image = imageService.deleteImage(id);
        return ResponseEntity.ok(imageMapper.toDTO(image));
    }
    // endregion
    // endregion
}