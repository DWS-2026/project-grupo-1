package es.apexexpeditions.library.dto.booking;


import java.util.List;

import es.apexexpeditions.library.dto.tour.TourResponseDTO;
import es.apexexpeditions.library.dto.user.UserBasicResponseDTO;

public record BookingResponseDTO(

        Long bookingId,
        UserBasicResponseDTO user,
        List<TourResponseDTO> tours,
        double totalPrice) {

}
