package es.apexexpeditions.library.dto.booking;

// simple summary of the aggregation all instances of user entity
public record BookingStatsDTO(
        long totalBookings,
        long openBookings,
        long closedBookings,
        double averageBookingPrice,
        double averageToursPerBooking

) {
}
