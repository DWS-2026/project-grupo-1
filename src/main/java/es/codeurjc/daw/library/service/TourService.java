package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TourService {

    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Tour save(Tour tour) {
        return tourRepository.save(tour);
    }
}
