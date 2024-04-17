package deltma.solutions.backend.controllers;

import deltma.solutions.backend.dto.CalendarMonthDTO;
import deltma.solutions.backend.dto.TimeRegisterRequestDTO;
import deltma.solutions.backend.dto.UserTotalTimeDTO;
import deltma.solutions.backend.services.TimeRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TimeRegisterController {

    @Autowired
    private TimeRegisterService timeRegisterService;

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    @PostMapping("/register-time")
    public ResponseEntity<String> registerTime(@RequestBody TimeRegisterRequestDTO timeRegisterRequestDTO) {
        try {
            timeRegisterService.registerTime(timeRegisterRequestDTO);
            return ResponseEntity.ok("Time registered successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error registering time " + e.getMessage());
        }
    }

    @GetMapping("/time-registrations")
    public ResponseEntity<List<TimeRegisterRequestDTO>> getTimeRegistrationsForSelectedMonth(CalendarMonthDTO calendarMonthDTO) {
        try {
            return ResponseEntity.ok(timeRegisterService.getTimeRegistrationsForSelectedMonth(calendarMonthDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/total-time-this-month")
    public ResponseEntity<Integer> getTotalTimeForSelectedMonth(CalendarMonthDTO calendarMonthDTO) {
        try {
            return ResponseEntity.ok(timeRegisterService.getTotalTimeForSelectedMonth(calendarMonthDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/time-registrations/{id}")
    public ResponseEntity<String> deleteTimeRegister(@PathVariable Long id) {
        try {
            timeRegisterService.deleteTimeRegister(id);
            return ResponseEntity.ok("Time register deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting time register " + e.getMessage());
        }
    }

    @PutMapping("/time-registrations/{id}")
    public ResponseEntity<String> updateTimeRegister(@RequestBody TimeRegisterRequestDTO timeRegisterRequestDTO) {
        try {
            timeRegisterService.updateTimeRegister(timeRegisterRequestDTO);
            return ResponseEntity.ok("Time registration updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating time registration: " + e.getMessage());
        }
    }

    @GetMapping("/users-total-time")
    public List<UserTotalTimeDTO> getAllUsersTotalTimePerMonth(@RequestParam int year, @RequestParam int month) {
        return timeRegisterService.getAllUsersTotalTimePerMonth(year, month);
    }

}
