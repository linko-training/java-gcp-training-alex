package com.alex.demo.controller;

import com.alex.demo.exception.FileAlreadyExistsException;
import com.alex.demo.exception.FileNotFoundException;
import com.alex.demo.model.FileContentResponse;
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

    @GetMapping("/{id}")
    public FileContentResponse getFile(@PathVariable String id) {
        return storageService.getFile(requireTxtFilename(id));
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public FileMetadata uploadFile(@RequestBody FileUploadRequest request) {
        String filename = requireTxtFilename(requireFilename(request));
        return storageService.store(filename, decodeContent(request));
    }

    @PutMapping("/{id}")
    public FileMetadata updateFile(@PathVariable String id,
                                   @RequestBody FileUploadRequest request) {
        String currentId = requireTxtFilename(id);
        String newFilename = request.getFile();
        if (newFilename == null || newFilename.isBlank()) {
            newFilename = currentId;
        } else {
            newFilename = requireTxtFilename(newFilename);
        }
        return storageService.updateFile(currentId, newFilename, decodeContent(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable String id) {
        storageService.deleteFile(requireTxtFilename(id));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(FileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyExists(FileAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    private String requireFilename(FileUploadRequest request) {
        if (request.getFile() == null || request.getFile().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'file' (nombre) es obligatorio");
        }
        return request.getFile();
    }

    private String requireTxtFilename(String filename) {
        if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo no válido");
        }
        if (!filename.toLowerCase().endsWith(".txt")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten archivos .txt");
        }
        return filename;
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
