package deltma.solutions.backend.controllers;

import deltma.solutions.backend.dto.*;
import deltma.solutions.backend.services.NewsService;
import deltma.solutions.backend.services.TemporaryUserService;
import deltma.solutions.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TemporaryUserService temporaryUserService;
    private final UserService userService;
    private final NewsService newsService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send-invitations")
    public ResponseEntity<String> sendInvitations(@RequestBody TemporaryUserDTO temporaryUserDTO) {
        try {
            temporaryUserService.validateAndSendInvitations(temporaryUserDTO);
            return ResponseEntity.ok("Invitations sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error sending invitations: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/share-news")
    public ResponseEntity<String> shareNews(@RequestBody ShareNewsRequest request) {
        try {
            newsService.shareNews(request.getEmail(), request.getNewsDTO());
            return ResponseEntity.ok("News shared successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error sharing news: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/edit-user/{email}")
    public ResponseEntity<String> editUser(@RequestBody UserProfileDTO request) {
        try {
            userService.editUser(request);
            return ResponseEntity.ok("User successfully edited");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error editing user: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        try {
            userService.deleteUser(email);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate-user/{email}")
    public ResponseEntity<?> deactivateUser(@PathVariable String email) {
        try {
            userService.deactivateUser(email);
            return ResponseEntity.ok("User deactivated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deactivating user: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate-user/{email}")
    public ResponseEntity<?> activateUser(@PathVariable String email) {
        try {
            userService.activateUser(email);
            return ResponseEntity.ok("User activated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error activating user: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/change-password/{email}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok("User password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error changing user password: " + e.getMessage());
        }
    }

}
