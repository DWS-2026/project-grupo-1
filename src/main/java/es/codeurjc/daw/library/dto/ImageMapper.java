package es.codeurjc.daw.library.dto;


import es.codeurjc.daw.library.model.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image image);
}