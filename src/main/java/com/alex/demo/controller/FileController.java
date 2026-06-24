package com.alex.demo.controller;

import com.alex.demo.exception.FileNotFoundException;
import com.alex.demo.model.FileMetadata;
import com.alex.demo.model.FileUploadRequest;
import com.alex.demo.service.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@Profile("!client")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public List<FileMetadata> listFiles() {
        return storageService.listFiles();
    }

    @PostMapping("/upload")
    public FileMetadata uploadFile(@RequestBody FileUploadRequest request) {
        return storageService.store(requireFilename(request), decodeContent(request));
    }

    @PutMapping("/{id}")
    public FileMetadata updateFile(@PathVariable String id,
                                   @RequestBody FileUploadRequest request) {
        String newFilename = request.getFile();
        if (newFilename == null || newFilename.isBlank()) {
            newFilename = id;
        }
        return storageService.updateFile(id, newFilename, decodeContent(request));
    }

    @DeleteMapping("/{id}")
    public void deleteFile(@PathVariable String id) {
        storageService.deleteFile(id);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(FileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    private String requireFilename(FileUploadRequest request) {
        if (request.getFile() == null || request.getFile().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'file' (nombre) es obligatorio");
        }
        return request.getFile();
    }

    private byte[] decodeContent(FileUploadRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'content' (base64) es obligatorio");
        }
        try {
            return Base64.getDecoder().decode(request.getContent());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'content' debe ser Base64 válido");
        }
    }
}
