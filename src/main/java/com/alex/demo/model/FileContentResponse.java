package com.alex.demo.model;

public class FileContentResponse {

    private String file;
    private String content;
    private long size;

    public FileContentResponse(String file, String content, long size) {
        this.file = file;
        this.content = content;
        this.size = size;
    }

    public String getFile() {
        return file;
    }

    public String getContent() {
        return content;
    }

    public long getSize() {
        return size;
    }
}
