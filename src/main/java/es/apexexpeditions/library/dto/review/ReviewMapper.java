package es.apexexpeditions.library.dto.review;

import es.apexexpeditions.library.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;

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

    @Named("toTourDTO")
    ReviewTourDTO toTourDTO(Review review);

    @Named("toTourDTOList")
    default List<ReviewTourDTO> toTourDTOList(List<Review> reviews) {
        if (reviews == null) return List.of();
        return reviews.stream().map(this::toTourDTO).toList();
    }

}
