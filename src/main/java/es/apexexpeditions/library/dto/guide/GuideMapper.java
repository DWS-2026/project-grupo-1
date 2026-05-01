package es.apexexpeditions.library.dto.guide;

import es.apexexpeditions.library.model.Guide;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GuideMapper {

    // 1. From Entity to DTO
    @Mapping(source = "tour.id", target = "tourId")
    @Mapping(source = "tour.name", target = "tourName")
    // MapStruct will use the isEnabled() method from the Guide entity
    @Mapping(source = "enabled", target = "enabled") 
    GuideResponseDTO toDTO(Guide guide);

    List<GuideResponseDTO> toDTOs(List<Guide> guides);

    // 2. From DTO to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    // MapStruct will use the setEnabled() method from the Guide entity
    @Mapping(target = "enabled", source = "enabled")
    Guide toDomain(GuideRequestDTO dto);
}