package com.alex.demo.service;

import com.alex.demo.exception.FileNotFoundException;
import com.alex.demo.model.FileMetadata;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Profile("cloud")
public class GcsStorageService implements StorageService {

    private final Storage storage;
    private final String bucketName;
    private final String prefix;

    public GcsStorageService(Storage storage,
                             @Value("${spring.cloud.gcp.storage.bucket}") String bucketName,
                             @Value("${storage.gcs.prefix:}") String prefix) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.prefix = normalizePrefix(prefix);
    }

    @Override
    public FileMetadata store(String filename, byte[] content) {
        String objectKey = objectKey(filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectKey)
                .setContentType("text/plain")
                .build();
        storage.create(blobInfo, content);
        return new FileMetadata(filename, content.length);
    }

    @Override
    public List<FileMetadata> listFiles() {
        Storage.BlobListOption prefixOption = prefix.isEmpty()
                ? null
                : Storage.BlobListOption.prefix(prefix);

        Iterable<Blob> blobs = prefixOption == null
                ? storage.list(bucketName).iterateAll()
                : storage.list(bucketName, prefixOption).iterateAll();

        return StreamSupport.stream(blobs.spliterator(), false)
                .map(blob -> new FileMetadata(filenameFromKey(blob.getName()), blob.getSize()))
                .collect(Collectors.toList());
    }

    @Override
    public FileMetadata updateFile(String id, String newFilename, byte[] content) {
        Blob blob = storage.get(BlobId.of(bucketName, objectKey(id)));
        if (blob == null) {
            throw new FileNotFoundException(id);
        }

        String newObjectKey = objectKey(newFilename);
        BlobInfo updated = BlobInfo.newBuilder(bucketName, newObjectKey)
                .setContentType("text/plain")
                .build();
        storage.create(updated, content);

        if (!objectKey(id).equals(newObjectKey)) {
            storage.delete(BlobId.of(bucketName, objectKey(id)));
        }

        return new FileMetadata(newFilename, content.length);
    }

    @Override
    public void deleteFile(String id) {
        boolean deleted = storage.delete(BlobId.of(bucketName, objectKey(id)));
        if (!deleted) {
            throw new FileNotFoundException(id);
        }
    }

    private String objectKey(String filename) {
        return prefix + filename;
    }

    private String filenameFromKey(String key) {
        if (prefix.isEmpty() || !key.startsWith(prefix)) {
            return key;
        }
        return key.substring(prefix.length());
    }

    private static String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "";
        }
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }
}
