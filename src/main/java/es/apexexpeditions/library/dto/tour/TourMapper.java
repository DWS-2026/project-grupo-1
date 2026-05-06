package es.apexexpeditions.library.dto.tour;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.apexexpeditions.library.dto.image.ImageMapper;
import es.apexexpeditions.library.dto.review.ReviewMapper;
import es.apexexpeditions.library.model.Guide;
import es.apexexpeditions.library.model.Tour;

@Mapper(componentModel = "spring", uses = { ImageMapper.class, ReviewMapper.class })
public interface TourMapper {

    @Mapping(target = "guideIds", source = "guides")
    TourResponseDTO toDTO(Tour tour);

    TourBookingDTO tourBookingDTO(Tour tour);

    default TourStatsDTO toStatsDTO(List<Tour> tours) {

        if (tours == null || tours.isEmpty()) {
            return new TourStatsDTO(0, 0, 0, 0, 0, 0, 0);
        }

        long totalTours = tours.size();
        long activeTours = 0;

        double maxPrice = Double.NEGATIVE_INFINITY;
        double minPrice = Double.POSITIVE_INFINITY;
        double totalRating = 0;
        double totalDuration = 0;

        for (Tour t : tours) {
            if (!t.isHidden())
                activeTours++;

            double price = t.getPrice();
            maxPrice = Math.max(maxPrice, price);
            minPrice = Math.min(minPrice, price);

            totalRating += t.getAverageRating();
            totalDuration += t.getDuration();
        }

        long disabledTours = totalTours - activeTours;

        return new TourStatsDTO(
                totalTours,
                activeTours,
                disabledTours,
                maxPrice,
                minPrice,
                totalRating / totalTours,
                totalDuration / totalTours);
    }

    List<TourResponseDTO> toDTOs(List<Tour> tours);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guides", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "tourImage", ignore = true)
    @Mapping(target = "selected", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    Tour toDomain(TourRequestDTO tourDTO);

    default List<Long> map(List<Guide> guides) {
        if (guides == null)
            return null;
        return guides.stream().map(Guide::getId).toList();
    }

}