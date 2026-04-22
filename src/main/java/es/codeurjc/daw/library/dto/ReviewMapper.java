package es.codeurjc.daw.library.dto;

import es.codeurjc.daw.library.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "tourId", source = "tour.id")
    @Mapping(target = "userId", source = "user.id")
    ReviewResponseDTO toDTO(Review review);

    Collection<ReviewResponseDTO> toDTOs(Collection<Review> reviews);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "hidden", ignore = true)
    Review toDomain(ReviewRequestDTO reviewDTO);
}
