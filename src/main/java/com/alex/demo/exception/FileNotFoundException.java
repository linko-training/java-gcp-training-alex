package com.alex.demo.exception;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String filename) {
        super("Archivo no encontrado: " + filename);
    }
}
