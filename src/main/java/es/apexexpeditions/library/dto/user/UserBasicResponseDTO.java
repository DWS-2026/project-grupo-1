package es.apexexpeditions.library.dto.user;



// equivalent to the admin modal that shows a brief preview
public record UserBasicResponseDTO (
        Long id,
        String name,
        String lastName,
        String email,
        String mainPhone,
        boolean enabled,
        Long imageId
) {}