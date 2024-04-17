package deltma.solutions.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String project;

    private boolean active;

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeRegister> timeRegisters;

    public Project(String project) {
        this.project = project;
        this.active = true;
    }

    public void removeTimeRegister(TimeRegister timeRegister) {
        timeRegisters.remove(timeRegister);
        timeRegister.setProject(null);
    }

}

