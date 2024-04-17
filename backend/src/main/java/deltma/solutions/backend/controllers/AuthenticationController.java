package deltma.solutions.backend.controllers;

import deltma.solutions.backend.dto.JwtAuthenticationResponse;
import deltma.solutions.backend.dto.SignInRequest;
import deltma.solutions.backend.dto.SignUpRequest;
import deltma.solutions.backend.dto.UserProfileDTO;
import deltma.solutions.backend.services.AuthenticationService;
import deltma.solutions.backend.services.TemporaryUserService;
import deltma.solutions.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Responsible for managing HTTP requests associated with user authentication. **/
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TemporaryUserService temporaryUserService;
    private final UserService userService;

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }

    @PostMapping("/register")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @GetMapping("/register/{uuid}")
    public ResponseEntity<?> registerUser(@PathVariable String uuid) {
        try {
            return ResponseEntity.ok(temporaryUserService.findTempUserByUuid(uuid));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error registering user.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        try {
            userService.resetUserPassword(email);
            return ResponseEntity.ok("New password sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error resetting password.");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileDTO>> getUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestParam String email) {
        try {
            return ResponseEntity.ok(userService.getUserProfileByUsername(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/profile/update-phone-number")
    public ResponseEntity<String> updatePhoneNumber(@RequestBody UserProfileDTO request) {
        try {
            userService.updatePhoneNumber(request);
            return ResponseEntity.ok("Phone number updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating phone number: " + e.getMessage());
        }
    }

    @PutMapping("/profile/update-first-name")
    public ResponseEntity<String> updateFirstName(@RequestBody UserProfileDTO request) {
        try {
            userService.updateFirstName(request);
            return ResponseEntity.ok("First name updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating first name: " + e.getMessage());
        }
    }

    @PutMapping("/profile/update-last-name")
    public ResponseEntity<String> updateLastName(@RequestBody UserProfileDTO request) {
        try {
            userService.updateLastName(request);
            return ResponseEntity.ok("Last name updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating last name: " + e.getMessage());
        }
    }

    @PutMapping("/profile/update-birthdate")
    public ResponseEntity<String> updateBirthdate(@RequestBody UserProfileDTO request) {
        try {
            userService.updateBirthdate(request);
            return ResponseEntity.ok("Birthdate updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating birthdate: " + e.getMessage());
        }
    }

    @GetMapping("/colleagues")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserProfileDTO> getUserByUsername(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getUserProfileByUsername(email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}