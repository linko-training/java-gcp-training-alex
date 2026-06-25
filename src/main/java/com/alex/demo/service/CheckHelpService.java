package com.alex.demo.service;

import com.alex.demo.config.CloudRunProperties;
import com.alex.demo.model.CheckHelpResponse;
import com.alex.demo.model.FileMetadata;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Profile("!client")
public class CheckHelpService {

    private final Environment environment;
    private final CloudRunProperties cloudRunProperties;
    private final Optional<StorageService> storageService;
    private final Optional<Storage> gcsStorage;
    private final String bucketName;
    private final String gcsPrefix;
    private final String projectId;
    private final String localPath;

    public CheckHelpService(Environment environment,
                            CloudRunProperties cloudRunProperties,
                            @Autowired(required = false) StorageService storageService,
                            @Autowired(required = false) Storage gcsStorage,
                            @Value("${spring.cloud.gcp.storage.bucket:}") String bucketName,
                            @Value("${storage.gcs.prefix:}") String gcsPrefix,
                            @Value("${spring.cloud.gcp.project-id:}") String projectId,
                            @Value("${storage.local.path:./local-data}") String localPath) {
        this.environment = environment;
        this.cloudRunProperties = cloudRunProperties;
        this.storageService = Optional.ofNullable(storageService);
        this.gcsStorage = Optional.ofNullable(gcsStorage);
        this.bucketName = bucketName;
        this.gcsPrefix = gcsPrefix;
        this.projectId = projectId;
        this.localPath = localPath;
    }

    public CheckHelpResponse check() {
        String profile = Arrays.stream(environment.getActiveProfiles())
                .findFirst()
                .orElse("default");
        boolean cloudProfile = "cloud".equals(profile);

        Map<String, Object> storageInfo = new LinkedHashMap<>();
        Map<String, String> serviceInfo = buildServiceInfo();

        if (cloudProfile) {
            storageInfo.put("type", "gcs");
            storageInfo.put("projectId", projectId);
            storageInfo.put("bucket", bucketName);
            storageInfo.put("prefix", gcsPrefix);
            return buildGcsCheck(profile, storageInfo, serviceInfo);
        }

        storageInfo.put("type", "local");
        storageInfo.put("path", Path.of(localPath).toAbsolutePath().normalize().toString());
        return buildLocalCheck(profile, storageInfo, serviceInfo);
    }

    private CheckHelpResponse buildGcsCheck(String profile,
                                            Map<String, Object> storageInfo,
                                            Map<String, String> serviceInfo) {
        if (gcsStorage.isEmpty()) {
            storageInfo.put("fileCount", 0);
            return new CheckHelpResponse(
                    "error",
                    profile,
                    false,
                    "Cliente GCS no configurado",
                    storageInfo,
                    serviceInfo
            );
        }

        try {
            gcsStorage.get().list(bucketName, Storage.BlobListOption.pageSize(1));
            List<FileMetadata> files = storageService.map(StorageService::listFiles).orElse(List.of());
            storageInfo.put("fileCount", files.size());
            return new CheckHelpResponse(
                    "ok",
                    profile,
                    true,
                    "GCS accesible",
                    storageInfo,
                    serviceInfo
            );
        } catch (Exception e) {
            storageInfo.put("fileCount", 0);
            return new CheckHelpResponse(
                    "error",
                    profile,
                    false,
                    "GCS no accesible: " + e.getMessage(),
                    storageInfo,
                    serviceInfo
            );
        }
    }

    private CheckHelpResponse buildLocalCheck(String profile,
                                              Map<String, Object> storageInfo,
                                              Map<String, String> serviceInfo) {
        Path dir = Path.of(localPath).toAbsolutePath().normalize();
        try {
            if (!Files.isDirectory(dir)) {
                throw new IllegalStateException("Directorio no encontrado: " + dir);
            }
            List<FileMetadata> files = storageService.map(StorageService::listFiles).orElse(List.of());
            storageInfo.put("fileCount", files.size());
            return new CheckHelpResponse(
                    "ok",
                    profile,
                    true,
                    "Almacenamiento local accesible",
                    storageInfo,
                    serviceInfo
            );
        } catch (Exception e) {
            storageInfo.put("fileCount", 0);
            return new CheckHelpResponse(
                    "error",
                    profile,
                    false,
                    "Almacenamiento local no accesible: " + e.getMessage(),
                    storageInfo,
                    serviceInfo
            );
        }
    }

    private Map<String, String> buildServiceInfo() {
        Map<String, String> serviceInfo = new LinkedHashMap<>();
        putIfPresent(serviceInfo, "publishedUrl", cloudRunProperties.getBaseUrl());
        putIfPresent(serviceInfo, "audience", cloudRunProperties.getAudience());
        putIfPresent(serviceInfo, "cloudRunService", System.getenv("K_SERVICE"));
        putIfPresent(serviceInfo, "cloudRunRevision", System.getenv("K_REVISION"));
        return serviceInfo;
    }

    private static void putIfPresent(Map<String, String> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }
}
