package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourRepository extends JpaRepository<Tour, Long> {}
