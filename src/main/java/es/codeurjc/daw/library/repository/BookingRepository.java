package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Booking;
import es.codeurjc.daw.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserAndCerradaFalse(User user);
    Page<Booking> findByUser(User user, Pageable pageable);
    Page<Booking> findByUserAndCerradaTrue(User user, Pageable pageable); // historial cerradas
    
}