package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    List<Guide> findByTourId(Long tourId);
}
