package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    // Una reserva puede tener varios tours (es tu "lista" o carrito)
   @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Tour> tours = new ArrayList<>();

    // Estado: false = carrito abierto, true = compra finalizada
    private boolean cerrada = false;

    public Reserva() {}

    public Reserva(User user) {
        this.user = user;
    }

    // Método para calcular el total
    public double getTotalPrice() {
        return tours.stream().mapToDouble(Tour::getPrice).sum();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public List<Tour> getTours() { return tours; }
    public void setTours(List<Tour> tours) { this.tours = tours; }
    
    public boolean isCerrada() { return cerrada; }
    public void setCerrada(boolean cerrada) { this.cerrada = cerrada; }
}