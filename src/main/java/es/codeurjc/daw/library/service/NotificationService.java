package es.codeurjc.daw.library.service;


import es.codeurjc.daw.library.model.Notification;

import es.codeurjc.daw.library.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class NotificationService {
    @Autowired
    private NotificationRepository repository;

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

    // mark notification as read
    public void markAsRead (Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setReadStatus (true);
            repository.save (n);
        });
    }
}