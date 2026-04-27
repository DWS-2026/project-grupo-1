package es.codeurjc.daw.library.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* Notification cases
 * User:
 *  - Created: by user (register.html) or admin (user-add.html): icon fas fa-user-plus and bg bg-success
 *  - Deleted: by user (profile.html) or admin (delete button on users.html): icon fas fa-user-minus and bg-warning
 */

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String icon;  // eg "fas fa-user", "fas fa-map-marked-alt"
    private String color; // eg "bg-primary", "bg-danger"

    private LocalDateTime date = LocalDateTime.now();

    private boolean readStatus = false;

    // jpa required empty constructor
    public Notification() {
    }

    // constructor
    public Notification(String message, String icon, String color) {
        this.message = message;
        this.icon = icon;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"));
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }
}