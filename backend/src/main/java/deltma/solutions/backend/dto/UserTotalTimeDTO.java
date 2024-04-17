package deltma.solutions.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTotalTimeDTO {
    private String email;
    private int totalTime;
    private Map<Long, Integer> totalPerProject;
}