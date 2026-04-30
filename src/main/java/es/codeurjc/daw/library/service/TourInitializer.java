package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

import es.codeurjc.daw.library.model.Image;
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
                8,
                2,
                true);

        createTour(
                "Viaje al pasado",
                "futuro1.1.jpg",
                "Explora ciudades del pasado...",
                2222.00,
                8,
                7,
                false);

        System.out.println(">>> Tours initialized");
    }

    private void createTour(String name, String imagePath, String description,
                            double price, int duration,
                            int numPeople, boolean hotelIncluded) {

        Tour tour = new Tour();
        tour.setName(name);

        try {
            byte[] bytes = Files.readAllBytes(
                    Paths.get ("src/main/resources/static/images/" + imagePath)
            );
            Image tour_image = new Image(bytes);
            tour.setTourImage (tour_image);
        } catch (Exception e) {
            System.err.println ("Error cargando la imagen para el tour: " + name);
            e.printStackTrace ();
            tour.setTourImage (null);
        }

        tour.setDescription(description);
        tour.setPrice(price);
        tour.setDuration(duration);
        tour.setNumPeople(numPeople);
        tour.setHotelIncluded(hotelIncluded);

        tourRepository.save(tour);
    }
}