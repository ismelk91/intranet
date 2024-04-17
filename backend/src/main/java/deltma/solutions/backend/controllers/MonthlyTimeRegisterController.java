package deltma.solutions.backend.controllers;

import deltma.solutions.backend.services.MonthlyTimeRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MonthlyTimeRegisterController {

    @Autowired
    private MonthlyTimeRegisterService monthlyTimeRegisterService;

    // For testing only. Remove class later
    @PostMapping("/save-and-reset-monthly-time")
    public ResponseEntity<Void> saveMonthlyTimeRegister() {
        try {
            monthlyTimeRegisterService.scheduleSaveMonthlyTimeRegisters();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}


