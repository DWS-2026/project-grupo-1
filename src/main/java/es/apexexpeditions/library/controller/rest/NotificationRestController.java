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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;
// endregion




@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notificaciones", description = "Gestión de alertas del sistema (Privado - Solo ADMIN)")
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
    @Operation(summary = "Obtener notificaciones recientes", description = "Devuelve una lista con las 10 notificaciones más recientes (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
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
    @Operation(summary = "Contar notificaciones no leídas", description = "Devuelve el número de mensajes sin leer en el sistema (Requiere ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conteo devuelto exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
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
    @Operation(summary = "Eliminar notificación", description = "Borra una notificación específica mediante su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminada con éxito"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // check if notification exists
        if (notificationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 2. deleteAllNotifications
    @Operation(summary = "Limpiar buzón", description = "Elimina de golpe todas las notificaciones del sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Buzón vaciado con éxito"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
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
    @Operation(summary = "Marcar como leída", description = "Cambia el estado de una notificación específica a 'leída'.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estado modificado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();   // case: not admin

        // check if notification exists
        if (notificationService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
    // endregion

    // region 2. markAllAsRead
    @Operation(summary = "Marcar todas como leídas", description = "Pone a 'leída' todas las notificaciones actuales del buzón.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estado general modificado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @PatchMapping("/read")
    public ResponseEntity<Void> markAllAsRead() {
        if (isNotAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        notificationService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }
    // endregion
    // endregion
}