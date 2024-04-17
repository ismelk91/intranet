package deltma.solutions.backend.controllers;

import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
public class SasTokenController {

    @GetMapping("/generate-sas-token")
    public String generateSasToken() {

        String connectionString = "DefaultEndpointsProtocol=https;AccountName=profil;AccountKey=nYN6+RS4f8P7z98luCAPeRLj6qJVo+UR5NYVn7d+dyEiYRcG1mR/wOKNOf+Ic4kh801lDej6KH0L+AStV/BtAg==;EndpointSuffix=core.windows.net";
        String containerName = "profilbild";
        String blobName = "admin1@admin.com_profile_picture.jpg";

        // Set the SAS token expiration time (15 minutes in this example)
        OffsetDateTime expirationTime = OffsetDateTime.now().plusMinutes(15);

        try {
            // Generate SAS token
            String sasToken = generateBlobSasToken(connectionString, containerName, blobName, expirationTime);
            return sasToken;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating SAS token";
        }
    }

    private String generateBlobSasToken(String connectionString, String containerName, String blobName, OffsetDateTime expirationTime) {
        BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(expirationTime, BlobSasPermission.parse("racwd"));

        // Generate SAS token directly as a string
        String sasToken = new BlobServiceClientBuilder().connectionString(connectionString)
                .buildClient()
                .getBlobContainerClient(containerName)
                .getBlobClient(blobName)
                .generateSas(sasSignatureValues);

        return sasToken;
    }
}
