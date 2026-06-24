package com.alex.demo.service;

import com.alex.demo.model.FileMetadata;

import java.util.List;

public interface StorageService {

    FileMetadata store(String filename, byte[] content);

    List<FileMetadata> listFiles();

    FileMetadata updateFile(String id, String newFilename, byte[] content);

    void deleteFile(String id);
}
