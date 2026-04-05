package es.codeurjc.daw.library.model;






// region =========== imports =================
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// endregion






/* Notification cases
 * User:
 *  - Created: by user (register.html) or admin (user-add.html): icon fas fa-user-plus and bg bg-success
 *  - Deleted: by user (profile.html) or admin (delete button on users.html): icon fas fa-user-minus and bg-warning
 */
@Entity
public class Notification {
    // region =========== id =================
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    // endregion





    // region =========== attributes =================
    private Long id;
    private String message;
    private String icon;  // eg "fas fa-user", "fas fa-map-marked-alt"
    private String color; // eg "bg-primary", "bg-danger"
    private LocalDateTime date = LocalDateTime.now();
    private boolean readStatus = false;
    // endregion





    // region =========== constructors =================
    // jpa required empty constructor
    public Notification() {}



    // constructor
    public Notification (String message, String icon, String color) {
        this.message = message;
        this.icon = icon;
        this.color = color;
    }
    // endregion





    // region =========== getters =================
    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
    public boolean isReadStatus() { return readStatus; }
    public String getFormattedDate() { return date.format (DateTimeFormatter.ofPattern("dd MMM, HH:mm")); }
    // endregion





    // region =========== setters =================
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }
    // endregion
}