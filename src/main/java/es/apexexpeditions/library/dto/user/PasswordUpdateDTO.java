package es.apexexpeditions.library.dto.user;



// equivalent to the fuctionality available in profile page
public record PasswordUpdateDTO (
        String oldPassword,
        String newPassword,
        String confirmPassword
) {}