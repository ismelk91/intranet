package deltma.solutions.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TimeRegisterRequestDTO {
    private Long id;
    private String email;
    private Integer workHours;
    private LocalDate date;
    private Long projectId;

    public TimeRegisterRequestDTO(Long id, Integer workHours, LocalDate date, String email, Long projectId) {
        this.id = id;
        this.workHours = workHours;
        this.date = date;
        this.email = email;
        this.projectId = projectId;
    }

    public int getMonth() {
        return date.getMonthValue();
    }

    public int getYear() {
        return date.getYear();
    }
}