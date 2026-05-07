package es.apexexpeditions.library.service;

import es.apexexpeditions.library.dto.tour.TourMapper;
import es.apexexpeditions.library.dto.tour.TourRequestDTO;
import es.apexexpeditions.library.dto.tour.TourStatsDTO;
import es.apexexpeditions.library.model.Image;
import es.apexexpeditions.library.model.Tour;
import es.apexexpeditions.library.repository.TourRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private TourMapper tourMapper;

    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    public Page<Tour> findAll(Pageable pageable) {
        return tourRepository.findAll(pageable);
    }

    public Tour findById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour not found: " + id));
    }

    public List<Tour> findByHiddenFalse() {
        return tourRepository.findByHiddenFalse();
    }

    public Page<Tour> findByHiddenFalse(Pageable pageable) {
        return tourRepository.findByHiddenFalse(pageable);
    }

    public Tour findByIdAndHiddenFalse(Long id) {
        return tourRepository.findByIdAndHiddenFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour not found: " + id));
    }

    public Tour save(Tour tour, MultipartFile file) throws Exception {
        Tour savedTour = tourRepository.save(tour);

        if (file != null && !file.isEmpty()) {
            savedTour = setTourImage(savedTour.getId(), file);
        }

        return savedTour;
    }

    public Tour save(Tour tour) {
        return tourRepository.save(tour);
    }

    public Tour deleteById(Long id) {
        Tour tour = this.findById(id);
        if (tour.getTourImage() != null) {
            imageService.deleteImage(tour.getTourImage().getId());
        }
        tourRepository.delete(tour);
        return tour;
    }

    public long count() {
        return tourRepository.count();
    }

    public Tour setTourImage(long id, MultipartFile imageFile) throws IOException {
        Image image = imageService.createImage(imageFile);
        Tour tour = this.findById(id);
        tour.setTourImage(image);
        tourRepository.save(tour);
        return tour;
    }

    public Tour removeTourImage(long id) {
        Tour tour = this.findById(id);
        tour.setTourImage(null);
        tourRepository.save(tour);
        return tour;
    }

    public Tour replaceTour(long id, TourRequestDTO dto, MultipartFile file) throws Exception {

        Tour existing = this.findById(id);

        existing.setName(dto.name());
        existing.setDescription(dto.description());
        existing.setPrice(dto.price());
        existing.setHidden(dto.hidden());
        existing.setDuration(dto.duration());
        existing.setNumPeople(dto.numPeople());
        existing.setHotelIncluded(dto.hotelIncluded());
        if (existing.getTourImage() != null) {
            imageService.replaceImageFile(existing.getTourImage().getId(), file);
        } else {
            Image newImage = imageService.createImage(file);
            existing.setTourImage(newImage);
        }

        return tourRepository.save(existing);
    }

    public Tour createTour(TourRequestDTO dto, MultipartFile image) throws Exception {

        Tour tour = tourMapper.toDomain(dto);

        Tour savedTour;

        if (image != null && !image.isEmpty()) {
            savedTour = this.save(tour, image);
        } else {
            savedTour = this.save(tour);
        }

        return savedTour;
    }

    public boolean existsById(long id) {
        return tourRepository.existsById(id);
    }

    public TourStatsDTO getTourStats() {
        List<Tour> tours = tourRepository.findAll();

        return tourMapper.toStatsDTO(tours);
    }

    public List<Tour> findByTourImage(Image image) {
        
        return tourRepository.findByTourImage(image);
    }

}