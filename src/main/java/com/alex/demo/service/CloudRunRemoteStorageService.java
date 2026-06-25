package com.alex.demo.service;

import com.alex.demo.client.CloudRunApiClient;
import com.alex.demo.model.FileContentResponse;
import com.alex.demo.model.FileMetadata;
import com.alex.demo.model.FileUploadRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@Profile("cloud")
@ConditionalOnProperty(prefix = "cloud-run.remote-api", name = "enabled", havingValue = "true")
public class CloudRunRemoteStorageService implements StorageService {

    private final CloudRunApiClient cloudRunApiClient;

    public CloudRunRemoteStorageService(CloudRunApiClient cloudRunApiClient) {
        this.cloudRunApiClient = cloudRunApiClient;
    }

    @Override
    public FileMetadata store(String filename, byte[] content) {
        return cloudRunApiClient.uploadFile(toRequest(filename, content));
    }

    @Override
    public List<FileMetadata> listFiles() {
        return cloudRunApiClient.listFiles();
    }

    @Override
    public FileContentResponse getFile(String id) {
        return cloudRunApiClient.getFile(id);
    }

    @Override
    public FileMetadata updateFile(String id, String newFilename, byte[] content) {
        return cloudRunApiClient.updateFile(id, toRequest(newFilename, content));
    }

    @Override
    public void deleteFile(String id) {
        cloudRunApiClient.deleteFile(id);
    }

    @Override
    public boolean exists(String id) {
        try {
            cloudRunApiClient.getFile(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static FileUploadRequest toRequest(String filename, byte[] content) {
        String encoded = Base64.getEncoder().encodeToString(content);
        return new FileUploadRequest(filename, encoded);
    }
}
