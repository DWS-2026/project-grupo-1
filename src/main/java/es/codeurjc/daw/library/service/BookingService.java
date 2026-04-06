package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Booking;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.model.User;
import es.codeurjc.daw.library.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // Get all bookings
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // Get all bookings (paginated)
    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    // Get booking by ID
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    // Get the open booking of a user (cart)
    public Optional<Booking> findOpenBookingByUser(User user) {
        return bookingRepository.findByUserAndCloseFalse(user);
    }

    // Get all bookings of a user (paginated)
    public Page<Booking> findPagedByUserId(User user, Pageable pageable) {
        return bookingRepository.findByUser(user, pageable);
    }

    // Get closed bookings of a user / history (paginated)
    public Page<Booking> findClosedBookingsByUser(User user, Pageable pageable) {
        return bookingRepository.findByUserAndCloseTrue(user, pageable);
    }

    // Get or create the open booking of a user
    public Booking getOrCreateOpenBooking(User user) {
        return bookingRepository.findByUserAndCloseFalse(user)
                .orElseGet(() -> bookingRepository.save(new Booking(user)));
    }

    // Add a tour to the open booking of the user
    public Booking addTour(User user, Tour tour) {
        Booking booking = getOrCreateOpenBooking(user);
        booking.getTours().add(tour);
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
        booking.setClose(true);
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
        return bookingRepository.findFirstByUserAndCloseTrueOrderByIdDesc(user);
    }
}