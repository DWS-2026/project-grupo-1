package es.apexexpeditions.library.model;






// region =========== imports =================
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// endregion






/* notification cases
 * user:
 *  - created: by user (register.html) or admin (user-add.html): icon fas fa-user-plus and bg bg-success
 *  - deleted: by user (profile.html) or admin (delete button on users.html): icon fas fa-user-minus and bg-warning
 */

// indexes increase query performance
@Table(name = "notifications", indexes = {
        @Index (name = "idx_notification_date", columnList = "date"),
        @Index (name = "idx_notification_read", columnList = "readStatus")
})
@Entity
public class Notification {
    // region =========== id =================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // endregion


    // region =========== attributes =================
    @NotBlank
    @Size (max = 500)
    private String message;

    @NotBlank
    @Size (max = 50)
    private String icon;   // eg "fas fa-user", "fas fa-map-marked-alt"

    @NotBlank
    @Size(max = 50)
    private String color;   // eg "bg-primary", "bg-danger"
    private LocalDateTime date = LocalDateTime.now();
    private boolean readStatus = false;
    // endregion


    // region =========== constructors =================
    public Notification() {
    }
    public Notification (String message, String icon, String color) {
        this.message = message;
        this.icon = icon;
        this.color = color;
    }
    // endregion


    // region =========== getters  =================
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
    // endregion


    // region =========== setters =================
    public void setReadStatus (boolean readStatus) {
        this.readStatus = readStatus;
    }
    // endregion
}