package es.apexexpeditions.library.repository;






// region =========== imports =================
import es.apexexpeditions.library.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
// endregion






public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // get 10 most recent notifications
    List<Notification> findFirst10ByOrderByDateDesc();
    // count unread notifications
    long countByReadStatusFalse();
}