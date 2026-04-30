package es.apexexpeditions.library.dto.notification;



public record NotificationDTO (
        Long id,
        String message,
        String icon,
        String color,
        String formattedDate,   // to use dd MMM, HH:mm format from entity
        boolean readStatus
) {}