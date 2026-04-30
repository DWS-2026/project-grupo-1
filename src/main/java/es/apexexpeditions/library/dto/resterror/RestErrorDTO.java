package es.apexexpeditions.library.dto.resterror;

import java.time.LocalDateTime;

public record RestErrorDTO(
        String timestamp, // using string because we convert ISO UTC format (to not reveal servers location)
        int status,
        String error,
        String message
) {}