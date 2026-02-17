package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.repository.GuideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuideService {

    private final GuideRepository guideRepository;

    public GuideService(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    public List<Guide> getGuidesByTour(Long tourId) {
        return guideRepository.findByTourId(tourId);
    }

    public Guide save(Guide guide) {
        return guideRepository.save(guide);
    }

    public Guide findById(Long id) {
        return guideRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Guide not found"));
    }
}
