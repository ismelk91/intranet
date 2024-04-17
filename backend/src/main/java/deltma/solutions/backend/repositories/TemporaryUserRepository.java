package deltma.solutions.backend.repositories;

import deltma.solutions.backend.models.TemporaryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryUserRepository extends JpaRepository<TemporaryUser, String> {
    TemporaryUser findByUuid(String uuid);
    TemporaryUser findByEmail(String email);
}

