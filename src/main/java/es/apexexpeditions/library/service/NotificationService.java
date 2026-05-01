package es.apexexpeditions.library.service;




// region =========== imports =================
import es.apexexpeditions.library.model.Notification;
import es.apexexpeditions.library.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
// endregion




@Service
public class NotificationService {
    // region =========== autowired =================
    @Autowired
    private NotificationRepository repository;
    // endregion



    // region =========== methods =================
    // create notification
    public void notify (String msg, String icon, String color) {
        repository.save (new Notification (msg, icon, color));
    }



    // retrieve 10 most recent notificactions
    public List<Notification> getRecent10() {
        return repository.findFirst10ByOrderByDateDesc();
    }



    // count unread notificactions
    public long getUnreadCount() {
        return repository.countByReadStatusFalse();
    }



    // mark single notification as read
    public void markAsRead (Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setReadStatus (true);
            repository.save (n);
        });
    }



    // mark all notifications as read
    public void markAllAsRead() {
        List<Notification> unread = repository.findByReadStatusFalse();
        unread.forEach(n -> n.setReadStatus(true));
        repository.saveAll(unread);
    }



    // delete single notification
    public void delete (Long id) {
        repository.deleteById(id);
    }



    // delete all notifications
    public void deleteAll() {
        repository.deleteAll();
    }

    // search for notfication by id (for api single notification operations: delete and mark)
    public Optional<Notification> findById(Long id) {
        return repository.findById(id);
    }
    // endregion
}