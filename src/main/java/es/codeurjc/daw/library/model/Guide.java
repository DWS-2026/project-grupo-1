package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "guide")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String lastName;
    private double price;
    private boolean Estado = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tour_id")
    @JsonBackReference
    private Tour tour;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    public Guide() {}

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public byte[] getProfilePicture() { 
        return profilePicture; 
    }
    public void setProfilePicture(byte[] profilePicture) { 
        this.profilePicture = profilePicture; 
    }

    public boolean isEnabled() {
        return Estado;
    }

    public void setEnabled(boolean enabled) {
        this.Estado = enabled;
    }
    public boolean isHasProfilePicture() {
        return this.profilePicture != null && this.profilePicture.length > 0;
    }
}
