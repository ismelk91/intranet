package deltma.solutions.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(name = "temporary_users")
public class TemporaryUser {

    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    public TemporaryUser(String email, String uuid) {
        this.email = email;
        this.uuid = uuid;
    }

}

