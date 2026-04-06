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

    // Obtener todas las reservas
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // Obtener todas las reservas (paginado)
    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    // Obtener reserva por ID
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    // Obtener la reserva abierta de un usuario (carrito)
    public Optional<Booking> findOpenBookingByUser(User user) {
        return bookingRepository.findByUserAndCerradaFalse(user);
    }

    // Obtener todas las reservas de un usuario (paginado)
    public Page<Booking> findPagedByUserId(User user, Pageable pageable) {
        return bookingRepository.findByUser(user, pageable);
    }

    // Obtener reservas cerradas de un usuario / historial (paginado)
    public Page<Booking> findClosedBookingsByUser(User user, Pageable pageable) {
        return bookingRepository.findByUserAndCerradaTrue(user, pageable);
    }

    // Obtener o crear la reserva abierta de un usuario
    public Booking getOrCreateOpenBooking(User user) {
        return bookingRepository.findByUserAndCerradaFalse(user)
                .orElseGet(() -> bookingRepository.save(new Booking(user)));
    }

    // Añadir un tour a la reserva abierta del usuario
    public Booking addTour(User user, Tour tour) {
        Booking booking = getOrCreateOpenBooking(user);
        booking.getTours().add(tour);
        return bookingRepository.save(booking);
    }

    // Eliminar un tour de la reserva abierta del usuario
    public Booking removeTour(User user, Tour tour) {
        Booking booking = getOrCreateOpenBooking(user);
        booking.getTours().removeIf(t -> t.getId().equals(tour.getId()));
        return bookingRepository.save(booking);
    }

    // Cerrar/confirmar la reserva
    public Booking closeBooking(User user) {
        Booking booking = getOrCreateOpenBooking(user);
        booking.setCerrada(true);
        return bookingRepository.save(booking);
    }

    // Guardar reserva
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Eliminar reserva por ID
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    public Optional<Booking> findLastClosedBookingByUser(User user) {
        return bookingRepository.findFirstByUserAndCerradaTrueOrderByIdDesc(user);
    }
}