package es.apexexpeditions.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

   @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Tour> tours = new ArrayList<>();

    private boolean close = false;

    public Booking() {}

    public Booking(User user) {
        this.user = user;
    }

    public double getTotalPrice() {
        return tours.stream().mapToDouble(Tour::getPrice).sum();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public List<Tour> getTours() { return tours; }
    public void setTours(List<Tour> tours) { this.tours = tours; }
    
    public boolean isClose() { return close; }
    public void setClose(boolean close) { this.close = close; }
}