package es.apexexpeditions.library.controller.rest;






// region =========== imports =================
import es.apexexpeditions.library.dto.notification.NotificationDTO;
import es.apexexpeditions.library.model.Notification;
import es.apexexpeditions.library.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
// endregion






@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationRestController {
    // region =========== autowired =================
    @Autowired
    private NotificationService notificationService;
    // endregion


    // region =========== GetMapping =================
    // region 1. getRecent
    // returns 10 most recent notifications
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getRecent() {
        List<Notification> notifications = notificationService.getRecent10();
        return ResponseEntity.ok(notifications.stream()
                .map(n -> new NotificationDTO(n.getId(), n.getMessage(), n.getIcon(),
                        n.getColor(), n.getFormattedDate(), n.isReadStatus()))
                .collect(Collectors.toList()));
    }
    // endregion


    // region 2. getUnreadCount
    // returns number of unread messages
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }
    // endregion
    // endregion


    // region =========== DeleteMapping =================
    // region 1. deleteNotification
    // removes notification from system
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion


    // region =========== PatchMapping =================
    // region 1. markAsRead
    // marks notification as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion
}