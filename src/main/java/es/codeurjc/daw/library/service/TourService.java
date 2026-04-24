package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Image;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.TourRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TourService {

    private final TourRepository tourRepository;

    @Autowired
    ImageService imageService;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    public Page<Tour> findAll(Pageable pageable) {
        return tourRepository.findAll(pageable);
    }

    public Tour findById(Long id) {
        return tourRepository.findById(id).orElse(null);
    }

    public List<Tour> findByHiddenFalse() {
        return tourRepository.findByHiddenFalse();
    }
    public Page<Tour> findByHiddenFalse(Pageable pageable) {
        return tourRepository.findByHiddenFalse(pageable);
    }

    public Tour findByIdAndHiddenFalse(Long id) {
        return tourRepository.findByIdAndHiddenFalse(id).orElse(null);
    }

    public Tour save(Tour tour, MultipartFile file) throws Exception {

        if (file != null && !file.isEmpty()) {
            tour.setTourImage(imageService.createImage(file));
        }

        return tourRepository.save(tour);
    }

    public Tour save(Tour tour) {
        return tourRepository.save(tour);
    }

    public void deleteById(Long id) {
        tourRepository.deleteById(id);
    }

    public long count() {
        return tourRepository.count();
    }
}