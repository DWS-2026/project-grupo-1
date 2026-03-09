package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.TourRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Service
public class TourService {

    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    public long count(){
        return tourRepository.count();
    }

    public Page<Tour> findAll(Pageable pageable) {
        return tourRepository.findAll(pageable);
    }

    public Tour findById(Long id) {
        return tourRepository.findById(id).orElse(null);
    }

    public Tour save(Tour tour, MultipartFile file) throws Exception {

        if (file != null && !file.isEmpty()) {
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            tour.setTourImage(base64);
        }

        return tourRepository.save(tour);
    }
}