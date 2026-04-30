package es.apexexpeditions.library.dto.user;



import java.util.List;


// dto shared between user and admin, applies restriction filters by role
public record UserUpdateDTO (
        String name,
        String lastName,
        String email,          // admin exclusive
        String mainPhone,
        String secondaryPhone,
        Boolean enabled,       // admin exclusive
        Double moneySpent,     // admin exclusive
        List<String> roles     // admin exclusive
) {}
