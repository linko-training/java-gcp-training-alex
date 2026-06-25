package com.alex.demo.model;

import java.util.Map;

public class CheckHelpResponse {

    private String status;
    private String profile;
    private boolean storageWorking;
    private String message;
    private Map<String, Object> storage;
    private Map<String, String> service;

    public CheckHelpResponse(String status,
                             String profile,
                             boolean storageWorking,
                             String message,
                             Map<String, Object> storage,
                             Map<String, String> service) {
        this.status = status;
        this.profile = profile;
        this.storageWorking = storageWorking;
        this.message = message;
        this.storage = storage;
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public String getProfile() {
        return profile;
    }

    public boolean isStorageWorking() {
        return storageWorking;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getStorage() {
        return storage;
    }

    public Map<String, String> getService() {
        return service;
    }
}
