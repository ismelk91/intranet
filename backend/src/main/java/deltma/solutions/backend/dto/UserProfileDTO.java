package deltma.solutions.backend.dto;


import deltma.solutions.backend.models.Role;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;
    private Role role;
    String profilePictureUrl;
}
