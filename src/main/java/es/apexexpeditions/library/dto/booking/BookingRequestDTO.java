package es.apexexpeditions.library.dto.booking;

import jakarta.validation.constraints.NotNull;
import java.util.List;


public record BookingRequestDTO(
        @NotNull List<Long> toursId) {
}
