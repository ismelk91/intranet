package deltma.solutions.backend.repositories;

import deltma.solutions.backend.dto.NewsDTO;
import deltma.solutions.backend.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findTop1ByOrderByDateDesc();

    News findNewsById(Long id);

}
