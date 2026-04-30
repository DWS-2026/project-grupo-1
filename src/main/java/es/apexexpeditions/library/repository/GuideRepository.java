package es.apexexpeditions.library.repository;

import es.apexexpeditions.library.model.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    List<Guide> findByTourId(Long tourId);
}
