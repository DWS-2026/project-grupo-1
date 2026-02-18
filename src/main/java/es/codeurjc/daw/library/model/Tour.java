package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private String image;

    @Lob
    private String description;

    private double price;

    private String duration;       // Nueva propiedad

    private int numPeople;         // Nueva propiedad

    private boolean hotelIncluded; // Nueva propiedad: true = Incluido, false = No incluido

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Guide> guides;

    public Tour() {}

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getNumPeople() { return numPeople; }
    public void setNumPeople(int numPeople) { this.numPeople = numPeople; }

    public boolean isHotelIncluded() { return hotelIncluded; }
    public void setHotelIncluded(boolean hotelIncluded) { this.hotelIncluded = hotelIncluded; }

    public List<Guide> getGuides() { return guides; }
    public void setGuides(List<Guide> guides) { this.guides = guides; }
}
