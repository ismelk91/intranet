package deltma.solutions.backend.config;

import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfig {

    private final String connectionString = "DefaultEndpointsProtocol=https;AccountName=profil;AccountKey=nYN6+RS4f8P7z98luCAPeRLj6qJVo+UR5NYVn7d+dyEiYRcG1mR/wOKNOf+Ic4kh801lDej6KH0L+AStV/BtAg==;EndpointSuffix=core.windows.net";

    @Bean
    public BlobServiceClientBuilder blobServiceClientBuilder() {
        return new BlobServiceClientBuilder().connectionString(connectionString);
    }
}
