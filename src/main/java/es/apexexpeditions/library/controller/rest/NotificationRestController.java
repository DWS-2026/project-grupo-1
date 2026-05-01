package es.apexexpeditions.library.controller.rest;






// region =========== imports =================
import es.apexexpeditions.library.dto.notification.NotificationDTO;
import es.apexexpeditions.library.model.Notification;
import es.apexexpeditions.library.model.User;
import es.apexexpeditions.library.service.NotificationService;
import es.apexexpeditions.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private UserService userService;
    // endregion


    // region =========== helpers =================
    // region isNotAdmin
    // BAC vuln fix
    private boolean isNotAdmin() {
        User loggedUser = userService.getLoggedUser();
        return loggedUser == null || !loggedUser.getRoles().contains("ADMIN");
    }
    // endregion
    // endregion


    // region =========== GetMapping =================
    // region 1. getRecent
    // returns 10 most recent notifications
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getRecent() {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();   // case: not admin
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
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();   // case: not admin
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }
    // endregion
    // endregion


    // region =========== DeleteMapping =================
    // region 1. deleteNotification
    // removes notification from system
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();   // case: not admin
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 2. deleteAllNotifications
    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications() {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        notificationService.deleteAll();
        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion


    // region =========== PatchMapping =================
    // region 1. markAsRead
    // marks single notification as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();   // case: not admin
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 2. markAllAsRead
    @PatchMapping("/read")
    public ResponseEntity<Void> markAllAsRead() {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        notificationService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion
}