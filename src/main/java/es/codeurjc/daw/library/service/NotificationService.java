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

    public void notify (String msg, String icon, String color) {
        repository.save (new Notification(msg, icon, color));
    }

    public List<Notification> getRecent() {
        return repository.findTop5ByOrderByDateDesc();
    }
}
