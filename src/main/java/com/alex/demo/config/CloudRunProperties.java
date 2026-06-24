package com.alex.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud-run")
public class CloudRunProperties {

    private String baseUrl = "https://linko-java-api-mid-430799291004.us-central1.run.app";
    private String audience = "https://linko-java-api-mid-430799291004.us-central1.run.app";
    private Client client = new Client();
    private RemoteApi remoteApi = new RemoteApi();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public RemoteApi getRemoteApi() {
        return remoteApi;
    }

    public void setRemoteApi(RemoteApi remoteApi) {
        this.remoteApi = remoteApi;
    }

    public boolean isAuthEnabled() {
        return client.isEnabled() || remoteApi.isEnabled();
    }

    public static class Client {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class RemoteApi {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
