package deltma.solutions.backend.dto;

import deltma.solutions.backend.models.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsDTO {

    private Long id;
    private String subject;
    private String message;
    private Date date;
    private Date deadline;

    public NewsDTO(News news) {
        this.id = news.getId();
        this.subject = news.getSubject();
        this.message = news.getMessage();
        this.date = news.getDate();
        this.deadline = news.getDeadline();
    }
}
