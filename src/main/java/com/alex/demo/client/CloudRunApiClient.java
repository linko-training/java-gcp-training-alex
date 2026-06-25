package com.alex.demo.client;

import com.alex.demo.config.CloudRunProperties;
import com.alex.demo.config.ConditionalOnCloudRunAuth;
import com.alex.demo.model.FileContentResponse;
import com.alex.demo.model.FileMetadata;
import com.alex.demo.model.FileUploadRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@ConditionalOnCloudRunAuth
public class CloudRunApiClient {

    private final RestClient restClient;

    public CloudRunApiClient(CloudRunProperties properties, CloudRunHttpLoggingInterceptor loggingInterceptor) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestInterceptor(loggingInterceptor)
                .build();
    }

    public List<FileMetadata> listFiles() {
        return restClient.get()
                .uri("/files")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public FileContentResponse getFile(String id) {
        return restClient.get()
                .uri("/files/{id}", id)
                .retrieve()
                .body(FileContentResponse.class);
    }

    public FileMetadata uploadFile(FileUploadRequest request) {
        return restClient.post()
                .uri("/files/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(FileMetadata.class);
    }

    public FileMetadata updateFile(String id, FileUploadRequest request) {
        return restClient.put()
                .uri("/files/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(FileMetadata.class);
    }

    public void deleteFile(String id) {
        restClient.delete()
                .uri("/files/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
