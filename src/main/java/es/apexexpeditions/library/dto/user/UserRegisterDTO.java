package es.apexexpeditions.library.dto.user;



public record UserRegisterDTO (
        String name,
        String lastName,
        String email,
        String password,
        String mainPhone,
        String secondaryPhone,
        boolean termsAccepted // equivalent to web form checkbox
) {}
