package com.alex.demo.service;

import com.alex.demo.exception.FileAlreadyExistsException;
import com.alex.demo.exception.FileNotFoundException;
import com.alex.demo.model.FileContentResponse;
import com.alex.demo.model.FileMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

@Service
@Profile("local")
public class LocalStorageService implements StorageService {

    private final Path storageDir;

    public LocalStorageService(@Value("${storage.local.path:./local-data}") String storagePath) {
        this.storageDir = Path.of(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio local: " + storageDir, e);
        }
    }

    @Override
    public FileMetadata store(String filename, byte[] content) {
        if (exists(filename)) {
            throw new FileAlreadyExistsException(filename);
        }

        try {
            Path target = resolveFile(filename);
            Files.write(target, content);
            return new FileMetadata(filename, content.length);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando archivo localmente", e);
        }
    }

    @Override
    public List<FileMetadata> listFiles() {
        try (Stream<Path> paths = Files.list(storageDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            return new FileMetadata(path.getFileName().toString(), Files.size(path));
                        } catch (IOException e) {
                            throw new RuntimeException("Error leyendo archivo: " + path, e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Error listando archivos locales", e);
        }
    }

    @Override
    public FileContentResponse getFile(String id) {
        Path file = resolveFile(id);
        if (!Files.exists(file)) {
            throw new FileNotFoundException(id);
        }

        try {
            byte[] content = Files.readAllBytes(file);
            String encoded = Base64.getEncoder().encodeToString(content);
            return new FileContentResponse(id, encoded, content.length);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo: " + id, e);
        }
    }

    @Override
    public FileMetadata updateFile(String id, String newFilename, byte[] content) {
        Path current = resolveFile(id);
        if (!Files.exists(current)) {
            throw new FileNotFoundException(id);
        }

        try {
            Path target = resolveFile(newFilename);
            Files.write(target, content);

            if (!id.equals(newFilename)) {
                Files.deleteIfExists(current);
            }

            return new FileMetadata(newFilename, content.length);
        } catch (IOException e) {
            throw new RuntimeException("Error actualizando archivo: " + id, e);
        }
    }

    @Override
    public void deleteFile(String id) {
        Path file = resolveFile(id);
        if (!Files.exists(file)) {
            throw new FileNotFoundException(id);
        }
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new RuntimeException("Error eliminando archivo: " + id, e);
        }
    }

    @Override
    public boolean exists(String id) {
        return Files.exists(resolveFile(id));
    }

    private Path resolveFile(String filename) {
        Path resolved = storageDir.resolve(filename).normalize();
        if (!resolved.startsWith(storageDir)) {
            throw new RuntimeException("Nombre de archivo no válido: " + filename);
        }
        return resolved;
    }
}
