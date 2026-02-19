package es.codeurjc.daw.library.repository;

import es.codeurjc.daw.library.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;




public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // retrieve 5 latest notifications (to not cover the whole page)
    List<Notification> findTop5ByOrderByDateDesc();
}