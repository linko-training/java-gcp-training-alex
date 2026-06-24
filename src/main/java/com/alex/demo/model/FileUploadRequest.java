package com.alex.demo.model;

public class FileUploadRequest {

    private String file;
    private String content;

    public FileUploadRequest() {
    }

    public FileUploadRequest(String file, String content) {
        this.file = file;
        this.content = content;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
