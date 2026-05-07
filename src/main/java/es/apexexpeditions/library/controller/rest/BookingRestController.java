package es.apexexpeditions.library.controller.rest;






// region =========== imports =================
import java.util.List;
import java.util.Optional;

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
// endregion





/**
 * API REST v1: booking management
 * * --- SECURITY AND ACCESS STRUCTURE ---
 * - ADMIN: view all closed bookings, access statistics, and delete bookings
 * - USER: manage their open cart (booking), checkout, and view their own closed bookings
 *
 * --- OPEN BOOKING ENDPOINTS (cart management) ---
 * - GET    /api/v1/bookings/me             : retrieves the authenticated user's current open booking
 * - POST   /api/v1/bookings/me/checkout    : closes the open booking
 * - POST   /api/v1/bookings/tours/{tourId} : adds a tour to the user's open booking
 * - DELETE /api/v1/bookings/tours/{tourId} : removes a tour from the user's open booking
 *
 * --- CLOSED BOOKING ENDPOINTS ---
 * - GET    /api/v1/bookings                : paginated list of closed bookings (all for admin, own for user)
 * - GET    /api/v1/bookings/{id}           : details of a closed booking. req: admin or owner
 * - DELETE /api/v1/bookings/{bookingId}    : permanently deletes a closed booking
 *
 * --- STATISTICS ENDPOINTS ---
 * - GET    /api/v1/bookings/stats          : returns global booking statistics
 */
@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Reservas", description = "Gestión de reservas de expediciones y tours")
public class BookingRestController {
    // region =========== autowired =================
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TourService tourService;
    // endregion



    // region =========== GetMapping =================
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
    // GET OPEN BOOKING
    // =========================================================
    @GetMapping("/me")
    public ResponseEntity<BookingResponseDTO> getMyOpenBooking() {

        User user = userService.getLoggedUser();

        Booking booking = bookingService.getOrCreateOpenBooking(user);

        return ResponseEntity.ok(bookingMapper.toDTO(booking));
    }

    // =========================================================
    // BOOKING STATS
    // =========================================================
    @GetMapping("/stats")
    public ResponseEntity<BookingStatsDTO> getBookingStats() {

        List<Booking> bookings;

        bookings = bookingService.findAllByCloseTrue();

        return ResponseEntity.ok(bookingMapper.toStatsDto(bookings));
    }
    // endregion




    // region =========== PostMapping =================
    // =========================================================
    // CLOSE (CHECKOUT) OPEN BOOKING
    // =========================================================
    @PostMapping("/me/checkout")
    public ResponseEntity<BookingResponseDTO> checkout() {

        User user = userService.getLoggedUser();

        Booking booking = bookingService.getOrCreateOpenBooking(user);

        if (booking.getTours().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes cerrar una reserva sin tours");
        }

        Booking closedBooking = bookingService.closeBooking(user);

        return ResponseEntity.ok(bookingMapper.toDTO(closedBooking));
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
    // endregion




    // region =========== DeleteMapping =================
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
    // DELETE A CLOSED BOOKING
    // =========================================================
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDTO> removeBooking(@PathVariable Long bookingId) {
        Booking booking = bookingService.findByIdAndCloseTrue(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        BookingResponseDTO dto = bookingMapper.toDTO(booking);

        bookingService.deleteById(bookingId);

        return ResponseEntity.ok(dto);
    }
    // endregion
}