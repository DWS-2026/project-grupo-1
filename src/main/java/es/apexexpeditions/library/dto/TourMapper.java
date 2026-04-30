package es.apexexpeditions.library.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.apexexpeditions.library.model.Guide;
import es.apexexpeditions.library.model.Tour;

@Mapper(componentModel = "spring", uses = { ImageMapper.class, ReviewMapper.class })
public interface TourMapper {

    @Mapping(target = "guideIds", source = "guides")
    TourResponseDTO toDTO(Tour tour);

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