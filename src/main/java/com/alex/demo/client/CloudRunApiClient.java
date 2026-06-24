package com.alex.demo.client;

import com.alex.demo.config.CloudRunProperties;
import com.alex.demo.model.FileMetadata;
import com.alex.demo.model.FileUploadRequest;
import com.alex.demo.service.GcpIdTokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "cloud-run.client", name = "enabled", havingValue = "true")
public class CloudRunApiClient {

    private final RestClient restClient;

    public CloudRunApiClient(CloudRunProperties properties, GcpIdTokenService tokenService) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestInterceptor((request, body, execution) -> {
                    try {
                        request.getHeaders().setBearerAuth(tokenService.getToken());
                    } catch (IOException e) {
                        throw new UncheckedIOException("No se pudo obtener el ID token de GCP", e);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    public List<FileMetadata> listFiles() {
        return restClient.get()
                .uri("/files")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
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
