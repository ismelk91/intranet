package deltma.solutions.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "monthly_time_registers")
public class MonthlyTimeRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer totalTime;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email", nullable = false)
    private User user;

    public MonthlyTimeRegister(Integer totalTime, Integer year, Integer month, User user) {
        this.totalTime = totalTime;
        this.year = year;
        this.month = month;
        this.user = user;
    }

}