package deltma.solutions.backend.controllers;

import deltma.solutions.backend.models.User;
import deltma.solutions.backend.repositories.UserRepository;
import deltma.solutions.backend.services.AzureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;
    private final AzureBlobStorageService azureBlobStorageService;


    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<String> uploadProfilePicture(MultipartFile profilePicture) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userRepository.findByEmail(userEmail);

            // Use AzureBlobStorageService to handle the file upload
            String pictureUrl = azureBlobStorageService.uploadProfilePictureToBlobStorage(user.getEmail(), profilePicture);
            user.setProfilePictureUrl(pictureUrl);

            userRepository.save(user);

            return ResponseEntity.ok("Profile picture uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error uploading profile picture");
        }
    }

    @PreAuthorize("hasRole('USER') OR hasRole('ADMIN')")
    @DeleteMapping("/delete-profile-picture")
    public ResponseEntity<String> deleteProfilePicture() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userRepository.findByEmail(userEmail);

            // Use AzureBlobStorageService to handle the file deletion
            azureBlobStorageService.deleteProfilePictureFromBlobStorage(user.getEmail());

            // Update the profile picture URL in the database
            user.setProfilePictureUrl(null);
            userRepository.save(user);

            return ResponseEntity.ok("Profile picture deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting profile picture");
        }
    }

}