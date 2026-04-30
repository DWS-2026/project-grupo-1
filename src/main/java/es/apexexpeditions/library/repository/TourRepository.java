package es.apexexpeditions.library.repository;

import es.apexexpeditions.library.model.Tour;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByHiddenFalse();

    Page<Tour> findByHiddenFalse(Pageable pageable);

    Optional<Tour> findByIdAndHiddenFalse(Long id);

}