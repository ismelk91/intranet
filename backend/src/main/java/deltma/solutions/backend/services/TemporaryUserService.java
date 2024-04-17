package deltma.solutions.backend.services;

import deltma.solutions.backend.dto.TemporaryUserDTO;
import deltma.solutions.backend.models.TemporaryUser;
import deltma.solutions.backend.repositories.TemporaryUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TemporaryUserService {

    private final TemporaryUserRepository temporaryUserRepository;
    private final EmailService emailService;
    private final ValidatorService validatorService;

    public void validateAndSendInvitations(TemporaryUserDTO temporaryUserDTO) {
        Set<String> validatedEmails = validatorService.validateEmails(temporaryUserDTO.getEmails());

        for (String email : validatedEmails) {
            if(isEmailAssociated(email)) {
                TemporaryUser temporaryUser = new TemporaryUser(email, UUID.randomUUID().toString());
                temporaryUserRepository.save(temporaryUser);

                String invitationLink = generateInvitationLink(UUID.fromString(temporaryUser.getUuid()));
                emailService.sendInvitation(email, invitationLink);
            }
        }
    }

    public boolean isEmailAssociated(String email) {
        return temporaryUserRepository.findByEmail(email) == null;
    }

    private String generateInvitationLink(UUID uuid) {
        return "http://localhost:8081/register/" + uuid;
    }

    public TemporaryUserDTO findTempUserByUuid(String uuid) {
        TemporaryUser temporaryUser = temporaryUserRepository.findByUuid(uuid);
        if (temporaryUser != null) {
            return new TemporaryUserDTO(Collections.singleton(temporaryUser.getEmail()), temporaryUser.getUuid());
        } else {
            throw new IllegalArgumentException("User not found with UUID: " + uuid);
        }
    }

    public void deleteTemporaryUserByEmail(String email) {
        TemporaryUser temporaryUser = temporaryUserRepository.findByEmail(email);
        if (temporaryUser != null) {
            temporaryUserRepository.delete(temporaryUser);
        } else {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
    }

    public boolean isCurrentUserTemporaryUser(String email) {
        return email != null && isEmailAssociated(email);
    }
}