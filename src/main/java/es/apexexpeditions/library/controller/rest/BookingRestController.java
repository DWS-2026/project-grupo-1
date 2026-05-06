package es.apexexpeditions.library.controller.rest;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.apexexpeditions.library.dto.booking.BookingMapper;
import es.apexexpeditions.library.dto.booking.BookingRequestDTO;
import es.apexexpeditions.library.dto.booking.BookingResponseDTO;
import es.apexexpeditions.library.model.Booking;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.BookingService;
import es.apexexpeditions.library.service.UserService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Reservas", description = "Gestión de reservas de expediciones y tours")
public class BookingRestController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private UserService userService;

    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================
    @Operation(summary = "Obtener todas las reservas", 
               description = "Devuelve una lista paginada de reservas. Si el usuario es ADMIN, obtiene todas; si es cliente, solo obtiene sus propias reservas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado o usuario no autenticado", content = @Content)
    })
    @GetMapping("")
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(
            @PageableDefault(size = 10) Pageable pageable) {

        User user = userService.getLoggedUser();
        Page<Booking> bookingsPage;

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else if (userService.isAdmin(user)) {
            bookingsPage = bookingService.findAll(pageable);
        } else {
            bookingsPage = bookingService.findPagedByUserId(user, pageable);
        }

        return ResponseEntity.ok(bookingsPage.map(bookingMapper::toDTO));
    }

    // =========================================================
    // GET SINGLE BOOKING
    // =========================================================
    @Operation(summary = "Obtener una reserva por ID", 
               description = "Permite recuperar los detalles de una reserva concreta si el usuario es el propietario o un administrador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva encontrada",
            content = { @Content(mediaType = "application/json", 
            schema = @Schema(implementation = BookingResponseDTO.class)) }),
        @ApiResponse(responseCode = "403", description = "No tienes permiso para ver esta reserva", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable long id) {

        Booking booking = bookingService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        User user = userService.getLoggedUser();

        boolean isAdmin = userService.isAdmin(user);
        boolean isOwner = booking.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver esta reserva");
        }

        return ResponseEntity.ok(bookingMapper.toDTO(booking));
    }

}
