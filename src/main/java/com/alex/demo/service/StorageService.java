package com.alex.demo.service;

import com.alex.demo.model.FileContentResponse;
import com.alex.demo.model.FileMetadata;

import java.util.List;

public interface StorageService {

    FileMetadata store(String filename, byte[] content);

    List<FileMetadata> listFiles();

    FileContentResponse getFile(String id);

    FileMetadata updateFile(String id, String newFilename, byte[] content);

    void deleteFile(String id);

    boolean exists(String id);
}
