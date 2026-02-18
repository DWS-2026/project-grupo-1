package es.codeurjc.daw.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.Tour;
import es.codeurjc.daw.library.repository.GuideRepository;
import es.codeurjc.daw.library.repository.TourRepository;

@Component
@Order(3)
public class GuideInitializer implements CommandLineRunner {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private TourRepository tourRepository;

    @Override
    public void run(String... args) {
        if (guideRepository.count() != 0)
            return;

        Tour tour = tourRepository.findAll().get(0);

        createGuide("Laura", "Méndez", 199.99, tour);
        createGuide("Carlos", "García", 249.99, tour);
        createGuide("María", "López", 299.99, tour);

        System.out.println(">>> Guides initialized");
    }

    private void createGuide(String name, String lastName, double price, Tour tour) {
        Guide g = new Guide();
        g.setName(name);
        g.setLastName(lastName);
        g.setPrice(price);
        g.setTour(tour);
        guideRepository.save(g);
    }
}

