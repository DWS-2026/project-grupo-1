package es.codeurjc.daw.library.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    public Review() {
    }

    public Review(User user, Tour tour, int rating, String description) {
        this.user = user;
        this.tour = tour;
        setRating(rating);
        this.description = description;
        this.hidden = false;
        this.creationDate = LocalDateTime.now();
    }
    @PrePersist
    public void prePersist() {
        if (this.creationDate == null) {
            this.creationDate = LocalDateTime.now();
        }
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
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    public boolean isStar1() { return rating >= 1; }
    public boolean isStar2() { return rating >= 2; }
    public boolean isStar3() { return rating >= 3; }
    public boolean isStar4() { return rating >= 4; }
    public boolean isStar5() { return rating >= 5; }

    public boolean isRating1() { return rating == 1; }
    public boolean isRating2() { return rating == 2; }
    public boolean isRating3() { return rating == 3; }
    public boolean isRating4() { return rating == 4; }
    public boolean isRating5() { return rating == 5; }

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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationDate() {
        if (creationDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return creationDate.format(formatter);
    }
}
