package com.alex.demo.exception;

public class FileAlreadyExistsException extends RuntimeException {

    public FileAlreadyExistsException(String filename) {
        super("El archivo ya existe: " + filename);
    }
}
