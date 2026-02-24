package es.codeurjc.daw.library.service;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;

@Service
public class TourService {

    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Tour save(Tour tour, MultipartFile file) throws Exception {

        if (file != null && !file.isEmpty()) {
            byte[] bytes = file.getBytes();
            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
            tour.setTourImage(base64);
        }

        return tourRepository.save(tour);
    }

    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    public Tour findById(Long id) {
        return tourRepository.findById(id).orElse(null);
    }
}
