package es.codeurjc.daw.library.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchas reviews para 1 usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-reviews")
    private User user;

    // Muchas reviews para 1 tour
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id")
    @JsonBackReference("tour-reviews")
    private Tour tour;

    // 1..5
    @Column(nullable = false)
    private int rating;

    @Lob
    @Column(nullable = false)
    private String description;

    private boolean hidden;

    public Review() {
    }

    public Review(User user, Tour tour, int rating, String description) {
        this.user = user;
        this.tour = tour;
        setRating(rating); // usa la validación que ya tienes
        this.description = description;
        this.hidden = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        // validación simple (evita valores fuera de rango)
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
