package com.alex.demo.controller;

import com.alex.demo.exception.FileNotFoundException;
import com.alex.demo.model.CheckHelpResponse;
import com.alex.demo.model.FileMetadata;
import com.alex.demo.model.FileUploadRequest;
import com.alex.demo.service.CheckHelpService;
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
@RequestMapping("*")
@Profile("!client")
public class checkhealthController {
    private final CheckHelpService checkHelpService;

    public checkhealthController(CheckHelpService checkHelpService) {
        this.checkHelpService = checkHelpService;
    }


    @GetMapping("/checkhealth")
    public CheckHelpResponse checkHelp() {
        return checkHelpService.check();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
