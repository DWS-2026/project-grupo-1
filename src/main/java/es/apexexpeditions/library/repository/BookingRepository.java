package es.apexexpeditions.library.repository;

import es.apexexpeditions.library.model.Booking;
import es.apexexpeditions.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByUserAndCloseTrueOrderByIdDesc(User user);
    Optional<Booking> findByUserAndCloseFalse(User user);
    Page<Booking> findByUser(User user, Pageable pageable);
    Page<Booking> findByUserAndCloseTrue(User user, Pageable pageable);
    
}