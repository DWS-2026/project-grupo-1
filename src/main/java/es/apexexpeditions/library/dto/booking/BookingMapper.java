package es.apexexpeditions.library.dto.booking;

import java.util.Collection;

import es.apexexpeditions.library.dto.tour.TourMapper;
import es.apexexpeditions.library.dto.user.UserMapper;
import es.apexexpeditions.library.model.Booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class, TourMapper.class })
public interface BookingMapper {
    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "tours", source = "tours")
    @Mapping(target = "totalPrice", source = "totalPrice")
    BookingResponseDTO toDTO(Booking booking);

    Collection<BookingResponseDTO> toDTOs(Collection<Booking> bookings);

    default BookingStatsDTO toStatsDto(Collection<Booking> bookings) {

        long total = bookings.size();
        long open = bookings.stream().filter(b -> !b.isClose()).count();
        long closed = total - open;

        double avgPrice = bookings.stream()
                .mapToDouble(Booking::getTotalPrice)
                .average()
                .orElse(0);

        double avgTours = bookings.stream()
                .mapToInt(b -> b.getTours().size())
                .average()
                .orElse(0);

        return new BookingStatsDTO(
                total,
                open,
                closed,
                avgPrice,
                avgTours);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tours", ignore = true)
    @Mapping(target = "close", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    Booking toDomain(BookingRequestDTO bookingDTO);
}
