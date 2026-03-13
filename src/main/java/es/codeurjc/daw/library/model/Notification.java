package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;




@Entity
public class Notification {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)

    // attributes start
    private Long id;
    private String message;
    private String icon;  // eg "fas fa-user", "fas fa-map-marked-alt"
    private String color; // eg "bg-primary", "bg-danger"
    private LocalDateTime date = LocalDateTime.now();
    private boolean readStatus = false;
    // attributes end

    // jpa required empty constructor
    public Notification() {}

    // constructor
    public Notification (String message, String icon, String color) {
        this.message = message;
        this.icon = icon;
        this.color = color;
    }

    // getters
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
    public boolean isReadStatus() { return readStatus; }
    public String getFormattedDate() { return date.format (DateTimeFormatter.ofPattern("dd MMM, HH:mm")); }

    // setters
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }
}