package deltma.solutions.backend.services;

import deltma.solutions.backend.dto.SignUpRequest;
import deltma.solutions.backend.dto.UserProfileDTO;
import deltma.solutions.backend.models.Role;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Service
public class ValidatorService {

    public void validateUser(SignUpRequest request) {
        validateEmail(request.getEmail());
        validateFirstName(request.getFirstName());
        validateLastName(request.getLastName());
        validatePassword(request.getPassword());
        validatePhoneNumber(request.getPhoneNumber());
    }

    public void validateUserProfile(UserProfileDTO userProfileDTO) {
        validateEmail(userProfileDTO.getEmail());
        validateFirstName(userProfileDTO.getFirstName());
        validateLastName(userProfileDTO.getLastName());
        validatePhoneNumber(userProfileDTO.getPhoneNumber());
    }

    // Validate email format
    public void validateEmail(String email) {
        if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
    public boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

    public Set<String> validateEmails(Set<String> emails) {
        Set<String> validEmails = new HashSet<>();
        for (String email : emails) {
            if (isValidEmail(email)) {
                validEmails.add(email);
            }
        }
        return validEmails;
    }
    // Validate so name contains only alphabetic characters
    public void validateFirstName(String firstName) {
        if (!firstName.matches("^[a-zA-Z]+$") || firstName.length() < 2 || firstName.length() > 30) {
            throw new IllegalArgumentException("Invalid name format");
        }
    }

    public void validateLastName(String lastName) {
        if (!lastName.matches("^[a-zA-Z]+$") || lastName.length() < 2 || lastName.length() > 30) {
            throw new IllegalArgumentException("Invalid name format");
        }
    }

    // Password criteria: at least 8 characters, 1 digit, 1 uppercase letter, 1 special character
    public void validatePassword(String password) {
        if (!password.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$")) {
            throw new IllegalArgumentException("Invalid password format");
        }
    }
    // Phone number is not null and is not empty
    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
    }

    // Validate if Role is valid
    public void validateRole(Role role) {
        if (role != Role.ROLE_ADMIN && role != Role.ROLE_USER) {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    // Input is not null or empty
    public void validateString(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    // Input is not null
    public void validateInteger(Integer input, String fieldName) {
        if (input == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    public void validateFileType(MultipartFile file) {
        if (!isValidFileType(file, "jpg") && !isValidFileType(file, "png")) {
            throw new IllegalArgumentException("Invalid file format. Only JPEG or PNG files are allowed.");
        }
    }

    private boolean isValidFileType(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String originalFilename = file.getOriginalFilename();
        return originalFilename != null && originalFilename.toLowerCase().endsWith("." + fileType);
    }

    public void validateFileTypeAndSize(MultipartFile file)  {
        // check file type
        validateFileType(file);

        // check file size
        long maxSizeInBytes = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSizeInBytes) {
            throw new IllegalArgumentException("File size exceeds the maximum allowed limit.");
        }
    }
}

