package es.codeurjc.daw.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchas reviews para 1 usuario
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Muchas reviews para 1 tour
    @ManyToOne(optional = false)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    // 0..5
    @Column(nullable = false)
    private int rating;

    @Lob
    private String description;

    public Review() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public int getRating() { return rating; }
    public void setRating(int rating) {
        // validación simple (evita valores fuera de rango)
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 0 and 5");
        }
        this.rating = rating;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
