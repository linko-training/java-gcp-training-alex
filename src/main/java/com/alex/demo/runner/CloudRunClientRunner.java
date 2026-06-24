package com.alex.demo.runner;

import com.alex.demo.client.CloudRunApiClient;
import com.alex.demo.model.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "cloud-run.client", name = "enabled", havingValue = "true")
public class CloudRunClientRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CloudRunClientRunner.class);

    private final CloudRunApiClient cloudRunApiClient;

    public CloudRunClientRunner(CloudRunApiClient cloudRunApiClient) {
        this.cloudRunApiClient = cloudRunApiClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        String action = args.containsOption("action")
                ? args.getOptionValues("action").getFirst()
                : "list";

        switch (action) {
            case "upload" -> upload(args);
            case "delete" -> delete(args);
            case "list" -> list();
            default -> throw new IllegalArgumentException("Acción no soportada: " + action);
        }
    }

    private void list() {
        List<FileMetadata> files = cloudRunApiClient.listFiles();
        log.info("Archivos en Cloud Run ({}):", files.size());
        files.forEach(file -> log.info("  - {} ({} bytes)", file.getFilename(), file.getSize()));
    }

    private void upload(ApplicationArguments args) {
        String filename = requireOption(args, "file");
        String content = args.containsOption("content")
                ? args.getOptionValues("content").getFirst()
                : "contenido de prueba";
        String encoded = Base64.getEncoder().encodeToString(content.getBytes());

        FileMetadata uploaded = cloudRunApiClient.uploadFile(
                new com.alex.demo.model.FileUploadRequest(filename, encoded));
        log.info("Archivo subido: {} ({} bytes)", uploaded.getFilename(), uploaded.getSize());
    }

    private void delete(ApplicationArguments args) {
        String id = requireOption(args, "file");
        cloudRunApiClient.deleteFile(id);
        log.info("Archivo eliminado: {}", id);
    }

    private static String requireOption(ApplicationArguments args, String name) {
        if (!args.containsOption(name)) {
            throw new IllegalArgumentException("Falta el parámetro --" + name);
        }
        return args.getOptionValues(name).getFirst();
    }
}
