package deltma.solutions.backend.services;

import deltma.solutions.backend.dto.CalendarMonthDTO;
import deltma.solutions.backend.dto.TimeRegisterRequestDTO;
import deltma.solutions.backend.dto.UserTotalTimeDTO;
import deltma.solutions.backend.models.Project;
import deltma.solutions.backend.models.TimeRegister;
import deltma.solutions.backend.models.User;
import deltma.solutions.backend.repositories.ProjectRepository;
import deltma.solutions.backend.repositories.TimeRegisterRepository;
import deltma.solutions.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeRegisterService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TimeRegisterRepository timeRegisterRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public void registerTime(TimeRegisterRequestDTO timeRegisterRequestDTO) {
        User user = userRepository.findByEmail(timeRegisterRequestDTO.getEmail());

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Project project = projectRepository.findById(timeRegisterRequestDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        TimeRegister timeRegister = new TimeRegister();
        timeRegister.setWorkHours(timeRegisterRequestDTO.getWorkHours());
        timeRegister.setDate(timeRegisterRequestDTO.getDate());
        timeRegister.setUser(user);
        timeRegister.setProject(project);

        timeRegisterRepository.save(timeRegister);
    }

    public List<TimeRegisterRequestDTO> getTimeRegistrationsForSelectedMonth(CalendarMonthDTO calendarMonthDTO) {
        List<TimeRegister> timeRegistrations = timeRegisterRepository.findByUserEmailAndDateYearAndDateMonth(
                calendarMonthDTO.getEmail(), calendarMonthDTO.getYear(), calendarMonthDTO.getMonth());

        return timeRegistrations.stream()
                .map(timeRegister -> new TimeRegisterRequestDTO(
                        timeRegister.getId(),
                        timeRegister.getWorkHours(),
                        timeRegister.getDate(),
                        timeRegister.getUser().getEmail(),
                        timeRegister.getProject() != null ? timeRegister.getProject().getId() : null
                ))
                .collect(Collectors.toList());
    }

    public Integer getTotalTimeForSelectedMonth(CalendarMonthDTO calendarMonthDTO) {
        List<TimeRegister> timeRegistrations = timeRegisterRepository.findByUserEmailAndDateYearAndDateMonth(
                calendarMonthDTO.getEmail(), calendarMonthDTO.getYear(), calendarMonthDTO.getMonth());

        return timeRegistrations.stream()
                .mapToInt(TimeRegister::getWorkHours)
                .sum();
    }

    public void deleteTimeRegister(Long id) {
        TimeRegister timeRegister = timeRegisterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Time registration not found for id: " + id));

        if (timeRegister.getProject() != null) {
            timeRegister.getProject().removeTimeRegister(timeRegister);
            timeRegister.setProject(null);
        }

        timeRegisterRepository.deleteById(id);
    }

    public void updateTimeRegister(TimeRegisterRequestDTO timeRegisterRequestDTO) {
        TimeRegister existingTimeRegister = timeRegisterRepository.findById(timeRegisterRequestDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Time registration not found for id: " + timeRegisterRequestDTO.getId()));

        User user = userRepository.findByEmail(timeRegisterRequestDTO.getEmail());

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Project project = projectRepository.findById(timeRegisterRequestDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        existingTimeRegister.setWorkHours(timeRegisterRequestDTO.getWorkHours());
        existingTimeRegister.setDate(timeRegisterRequestDTO.getDate());
        existingTimeRegister.setUser(user);
        existingTimeRegister.setProject(project);

        timeRegisterRepository.save(existingTimeRegister);
    }

    public List<UserTotalTimeDTO> getAllUsersTotalTimePerMonth(int year, int month) {
        List<User> allUsers = userRepository.findAll();

        List<TimeRegister> timeRegistrations = timeRegisterRepository.findByDateYearAndDateMonth(year, month);

        Map<String, UserTotalTimeDTO> userTotalTimeMap = allUsers.stream()
                .collect(Collectors.toMap(User::getEmail, user -> {
                    UserTotalTimeDTO userTotalTimeDTO =
                            new UserTotalTimeDTO(user.getEmail(), 0, new HashMap<>());

                    List<Project> allProjects = projectRepository.findAll();
                    for (Project project : allProjects) {
                        userTotalTimeDTO.getTotalPerProject().put(project.getId(), 0);
                    }
                    return userTotalTimeDTO;
                }));

        for (TimeRegister timeRegister : timeRegistrations) {
            String userEmail = timeRegister.getUser().getEmail();
            int workHours = timeRegister.getWorkHours();

            UserTotalTimeDTO userTotalTimeDTO = userTotalTimeMap.get(userEmail);
            userTotalTimeDTO.setTotalTime(userTotalTimeDTO.getTotalTime() + workHours);

            if (timeRegister.getProject() != null) {
                long projectId = timeRegister.getProject().getId();
                Map<Long, Integer> totalPerProject = userTotalTimeDTO.getTotalPerProject();
                int totalHoursForProject = totalPerProject.getOrDefault(projectId, 0);
                totalPerProject.put(projectId, totalHoursForProject + workHours);
            }
        }

        return new ArrayList<>(userTotalTimeMap.values());
    }

}

