package es.codeurjc.daw.library.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.daw.library.model.Guide;
import es.codeurjc.daw.library.model.Tour;

@Mapper(componentModel = "spring", uses = {ImageMapper.class, ReviewMapper.class})
public interface TourMapper {

    @Mapping(target = "guideIds", expression = "java(mapGuideIds(tour.getGuides()))")
    TourDTO toDTO(Tour tour);

    List<TourDTO> toDTOs(Collection<Tour> tour);

    @Mapping(target = "guides", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "tourImage", ignore = true)
    @Mapping(target = "selected", ignore = true)
    Tour toDomain(TourDTO tourDTO);

    default List<Long> mapGuideIds(List<Guide> guides) {
        if (guides == null) return null;
        return guides.stream().map(Guide::getId).toList();
    }
}