package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.TourRepository;

@Component
@Order(2)
public class TourInitializer implements CommandLineRunner {

    @Autowired
    private TourRepository tourRepository;

    @Override
    public void run(String... args) {
        if (tourRepository.count() != 0)
            return;

        createTour(
            "Viaje al futuro",
            "imagen1.jpg",
            "Explora ciudades futuristas...",
            4349.00,
            "3 días",
            2,
            true
        );

        createTour(
            "Viaje al pasado",
            "imagen2.jpg",
            "Explora ciudades del pasado...",
            2222.00,
            "8 días",
            7,
            false
        );

        System.out.println(">>> Tours initialized");
    }

    private void createTour(String name, String image, String description, double price, String duration, int numPeople, boolean hotelIncluded) {
        Tour tour = new Tour();
        tour.setName(name);
        tour.setImage(image);
        tour.setDescription(description);
        tour.setPrice(price);
        tour.setDuration(duration);
        tour.setNumPeople(numPeople);
        tour.setHotelIncluded(hotelIncluded);
        tourRepository.save(tour);
    }
}
