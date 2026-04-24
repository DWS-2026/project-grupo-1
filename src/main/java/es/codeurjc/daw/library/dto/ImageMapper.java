package es.codeurjc.daw.library.dto;


import es.codeurjc.daw.library.model.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
     default ImageDTO toDTO(Image image) {
        if (image == null) return null;
        return new ImageDTO(image.getId());
    }

    default Image toDomain(ImageDTO dto) {
        if (dto == null) return null;

        Image img = new Image();
        img.setId(dto.id());
        return img;
    }
}