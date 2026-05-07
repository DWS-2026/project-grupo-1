package es.apexexpeditions.library.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tours")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    @Lob
    private Image tour_image;

    @Lob
    private String description;

    private double price;
    private int duration;

    // new property
    private int numPeople;

    // new property
    private boolean hotelIncluded; // true = included, false = not included

    private boolean hidden = false;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tour-guides")
    private List<Guide> guides = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tour-reviews")
    private List<Review> reviews = new ArrayList<>();

    @Transient
    private boolean selected;

    public Tour() {
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getTourImage() {
        return tour_image;

    }

    public void setTourImage(Image image) {
        this.tour_image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public boolean isHotelIncluded() {
        return hotelIncluded;
    }

    public void setHotelIncluded(boolean hotelIncluded) {
        this.hotelIncluded = hotelIncluded;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean bool) {
        this.hidden = bool;
    }

    public List<Guide> getGuides() {
        return guides;
    }

    public void setGuides(List<Guide> guides) {
        this.guides = guides;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        if (review != null) {
            reviews.add(review);
            review.setTour(this);
        }
    }

    public void removeReview(Review review) {
        if (review != null) {
            reviews.remove(review);
            review.setTour(null);
        }
    }

    public double getAverageRating() {

        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }

        return reviews.stream().mapToInt(Review::getRating).average().orElse(0);
    }
}
