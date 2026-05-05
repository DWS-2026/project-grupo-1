package es.apexexpeditions.library.dto.user;




// use: show brief summary of key user info
// role req: admin
public record UserBasicResponseDTO (
        Long id,
        String name,
        String lastName,
        String email,
        String mainPhone,
        boolean enabled,
        Long imageId
) {}