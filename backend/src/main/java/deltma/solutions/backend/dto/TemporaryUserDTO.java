package deltma.solutions.backend.dto;

import lombok.Data;
import java.util.Set;

@Data
public class TemporaryUserDTO {

    private Set<String> emails;
    private String uuid;

    public TemporaryUserDTO(Set<String> emails, String uuid) {
        this.emails = emails;
        this.uuid = uuid;
    }
}

