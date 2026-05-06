package es.apexexpeditions.library.repository;

import es.apexexpeditions.library.model.Booking;
import es.apexexpeditions.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    Page<Booking> findAllByCloseTrue(Pageable pageable);

    List<Booking> findAllByCloseTrue();

    Optional<Booking> findByIdAndCloseTrue(Long id);

    Optional<Booking> findByUserAndCloseFalse(User user);

    Optional<Booking> findFirstByUserAndCloseTrueOrderByClosedAtDesc(User user);

    List<Booking> findByUserAndCloseTrue(User user);

    Page<Booking> findByUserAndCloseTrue(User user, Pageable pageable);


    
}