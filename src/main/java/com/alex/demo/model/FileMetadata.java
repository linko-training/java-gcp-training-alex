package com.alex.demo.model;

public class FileMetadata {
    private String filename;
    private long size;

    public FileMetadata(String filename, long size) {
        this.filename = filename;
        this.size = size;
    }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
}
