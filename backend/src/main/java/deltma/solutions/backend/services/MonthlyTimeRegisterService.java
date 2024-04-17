package deltma.solutions.backend.services;

import deltma.solutions.backend.dto.MonthlyTimeDTO;
import deltma.solutions.backend.models.MonthlyTimeRegister;
import deltma.solutions.backend.models.TimeRegister;
import deltma.solutions.backend.models.User;
import deltma.solutions.backend.repositories.MonthlyTimeRegisterRepository;
import deltma.solutions.backend.repositories.TimeRegisterRepository;
import deltma.solutions.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
public class MonthlyTimeRegisterService {

    @Autowired
    private MonthlyTimeRegisterRepository monthlyTimeRegisterRepository;
    @Autowired
    private TimeRegisterRepository timeRegisterRepository;
    @Autowired
    private UserRepository userRepository;

    private void saveMonthlyTimeRegisters(MonthlyTimeDTO monthlyTimeDTO) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<TimeRegister> timeRegistrations = timeRegisterRepository.findByUserEmailAndDateYearAndDateMonth(
                    user.getEmail(), monthlyTimeDTO.getYear(), monthlyTimeDTO.getMonth());

            int totalTimeForUser = timeRegistrations.stream()
                    .mapToInt(TimeRegister::getWorkHours)
                    .sum();

            MonthlyTimeRegister monthlyTimeRegister = monthlyTimeRegisterRepository
                    .findByUserAndYearAndMonth(user, monthlyTimeDTO.getYear(), monthlyTimeDTO.getMonth());

            if (monthlyTimeRegister == null) {
                monthlyTimeRegister = new MonthlyTimeRegister(0, monthlyTimeDTO.getYear(), monthlyTimeDTO.getMonth(), user);
            }

            monthlyTimeRegister.setTotalTime(totalTimeForUser);
            monthlyTimeRegisterRepository.save(monthlyTimeRegister);
        }
    }

    @Scheduled(cron = "0 0 0 10 * ?")
    public void scheduleSaveMonthlyTimeRegisters() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastMonth = currentDate.minusMonths(1);
        int lastMonthYear = lastMonth.getYear();
        int lastMonthValue = lastMonth.getMonthValue();

        MonthlyTimeDTO monthlyTimeDTO = MonthlyTimeDTO.builder()
                .year(lastMonthYear)
                .month(lastMonthValue)
                .build();

        saveMonthlyTimeRegisters(monthlyTimeDTO);
    }

}