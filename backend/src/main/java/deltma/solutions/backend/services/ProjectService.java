package deltma.solutions.backend.services;

import deltma.solutions.backend.dto.ProjectDTO;
import deltma.solutions.backend.models.Project;
import deltma.solutions.backend.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(ProjectDTO projectDTO) {
        Project project = new Project(projectDTO.getProject());
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    public Project updateProject(ProjectDTO projectDTO) {
        Project project = projectRepository.findById(projectDTO.getId())
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectDTO.getId()));
        project.setProject(projectDTO.getProject());
        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        project.getTimeRegisters().forEach(timeRegister -> timeRegister.setProject(null));

        projectRepository.delete(project);
        System.out.println("Project deleted successfully");
    }

    @Transactional
    public void inactivateProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        project.setActive(false);

        projectRepository.save(project);
        System.out.println("Project inactivated successfully");
    }

    @Transactional
    public void activateProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        project.setActive(true);

        projectRepository.save(project);
        System.out.println("Project activated successfully");
    }

}