package es.apexexpeditions.library.repository;

import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.model.Tour;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByHiddenFalse();

    Page<Tour> findByHiddenFalse(Pageable pageable);

    Optional<Tour> findByIdAndHiddenFalse(Long id);

    @Query("SELECT t FROM Tour t WHERE t.tour_image = :image")
    List<Tour> findByTourImage(@Param("image")Image image);
}