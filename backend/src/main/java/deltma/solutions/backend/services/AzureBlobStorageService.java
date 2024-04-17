package deltma.solutions.backend.services;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AzureBlobStorageService {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String storePicture(String email, InputStream pictureInputStream) {
        // store picture with timestamp
        return "azure_blob_url/pictures/" + email + "_timestamp.jpg";
    }

    public String uploadProfilePictureToBlobStorage(String username, MultipartFile profilePicture) {
        try {
            // Validate the profile picture
            validateProfilePicture(profilePicture);

            // Get the input stream from the uploaded file
            InputStream pictureInputStream = profilePicture.getInputStream();
            String blobName = username + "_profile_picture.jpg";

            // Create a BlobServiceClient
            BlobServiceClientBuilder blobServiceClientBuilder = new BlobServiceClientBuilder()
                    .connectionString(getAzureStorageConnectionString());
            BlobServiceClient blobServiceClient = blobServiceClientBuilder.buildClient();

            // Get a reference to a container
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Upload the image to the container
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            blobClient.upload(pictureInputStream, profilePicture.getSize(), true);

            // Get the URL of the uploaded image
            return blobClient.getBlobUrl();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploading profile picture");
        }
    }

    public void deleteProfilePictureFromBlobStorage(String username) {
        try {
            String blobName = username + "_profile_picture.jpg";

            // Create a BlobServiceClient
            BlobServiceClientBuilder blobServiceClientBuilder = new BlobServiceClientBuilder()
                    .connectionString(getAzureStorageConnectionString());
            BlobServiceClient blobServiceClient = blobServiceClientBuilder.buildClient();

            // Get a reference to a container
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Get a reference to the blob
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            // Delete the blob
            blobClient.delete();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting profile picture from blob storage");
        }
    }

    // Validation method for profile picture
    private void validateProfilePicture(MultipartFile profilePicture) {
        if (profilePicture.isEmpty()) {
            throw new IllegalArgumentException("Profile picture is required");
        }

    }

    private String getAzureStorageConnectionString() {
        return "DefaultEndpointsProtocol=https;AccountName=" + accountName + ";AccountKey=" + accountKey + ";EndpointSuffix=core.windows.net";
    }
}
