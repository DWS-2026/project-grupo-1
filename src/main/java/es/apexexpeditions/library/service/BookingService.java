package es.apexexpeditions.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.apexexpeditions.library.model.Booking;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    // Get all bookings
    public List<Booking> findAllByCloseTrue() {
        return bookingRepository.findAllByCloseTrue();
    }

    // Get all bookings (paginated)
    public Page<Booking> findAllByCloseTrue(Pageable pageable) {
        return bookingRepository.findAllByCloseTrue(pageable);
    }

    // Get booking by ID
    public Optional<Booking> findByIdAndCloseTrue(Long id) {
        return bookingRepository.findByIdAndCloseTrue(id);
    }

    // Get or create the open booking of a user
    public Booking getOrCreateOpenBooking(User user) {
        return bookingRepository.findByUserAndCloseFalse(user)
                .orElseGet(() -> new Booking(user));
    }

    // Add a tour to the open booking of the user
    public Booking addTour(User user, Tour tour) {
        Booking booking = getOrCreateOpenBooking(user);
        if (booking.getTours().stream()
                .noneMatch(t -> t.getId().equals(tour.getId()))) {
            booking.getTours().add(tour);
        }
        return bookingRepository.save(booking);
    }

    // Remove a tour from the open booking of the user
    public Booking removeTour(User user, Tour tour) {
        Booking booking = getOrCreateOpenBooking(user);

        booking.getTours().removeIf(t -> t.getId().equals(tour.getId()));

        return bookingRepository.save(booking);
    }

    // Close/confirm the booking
    public Booking closeBooking(User user) {
        Booking booking = getOrCreateOpenBooking(user);
        if (!booking.isClose() && !booking.getTours().isEmpty()) {
            booking.setClose(true);
            booking.setClosedAt(LocalDateTime.now());
            user.addMoneySpent(booking.getTotalPrice());
            userService.save(user);
        }
        return bookingRepository.save(booking);
    }

    // Save booking
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Delete booking by ID
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    public Optional<Booking> findLastClosedBookingByUser(User user) {
        return bookingRepository.findFirstByUserAndCloseTrueOrderByClosedAtDesc(user);
    }

    public Page<Booking> findByUserAndCloseTrue(User user, Pageable pageable) {
        return bookingRepository.findByUserAndCloseTrue(user, pageable);
    }

}