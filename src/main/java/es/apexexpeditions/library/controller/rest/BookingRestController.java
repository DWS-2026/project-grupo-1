package es.apexexpeditions.library.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.apexexpeditions.library.dto.booking.BookingMapper;
import es.apexexpeditions.library.dto.booking.BookingResponseDTO;
import es.apexexpeditions.library.dto.booking.BookingStatsDTO;
import es.apexexpeditions.library.model.Booking;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.BookingService;
import es.apexexpeditions.library.service.TourService;
import es.apexexpeditions.library.service.UserService;
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

    @Autowired
    private TourService tourService;

    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================
    @GetMapping
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(
            @PageableDefault(size = 10) Pageable pageable) {

        User user = userService.getLoggedUser();

        Page<Booking> bookings;

        if (userService.isAdmin(user)) {
            bookings = bookingService.findAllByCloseTrue(pageable);
        } else {
            bookings = bookingService.findByUserAndCloseTrue(user, pageable);
        }

        return ResponseEntity.ok(bookings.map(bookingMapper::toDTO));
    }

    // =========================================================
    // GET BY ID
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {

        User user = userService.getLoggedUser();

        Booking booking = bookingService.findByIdAndCloseTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        boolean isAdmin = userService.isAdmin(user);
        boolean isOwner = booking.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(bookingMapper.toDTO(booking));
    }

    // =========================================================
    // ADD TOUR TO OPEN BOOKING
    // =========================================================
    @PostMapping("/tours/{tourId}")
    public ResponseEntity<BookingResponseDTO> addTour(@PathVariable Long tourId) {

        User user = userService.getLoggedUser();

        Tour tour = tourService.findByIdAndHiddenFalse(tourId);

        Booking booking = bookingService.addTour(user, tour);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingMapper.toDTO(booking));
    }

    // =========================================================
    // REMOVE TOUR FROM OPEN BOOKING
    // =========================================================
    @DeleteMapping("/tours/{tourId}")
    public ResponseEntity<BookingResponseDTO> removeTour(@PathVariable Long tourId) {

        User user = userService.getLoggedUser();

        Tour tour = tourService.findByIdAndHiddenFalse(tourId);

        Booking booking = bookingService.removeTour(user, tour);

        return ResponseEntity.ok(bookingMapper.toDTO(booking));
    }

    // =========================================================
    // DELETE BOOKING
    // =========================================================
    @GetMapping("/stats")
    public ResponseEntity<BookingStatsDTO> getBookingStats() {

        List<Booking> bookings;

        bookings = bookingService.findAllByCloseTrue();

        return ResponseEntity.ok(bookingMapper.toStatsDto(bookings));
    }
}