package com.alex.demo.service;

import com.alex.demo.config.CloudRunProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@ConditionalOnProperty(prefix = "cloud-run.client", name = "enabled", havingValue = "true")
public class GcpIdTokenService {

    private final IdTokenCredentials idTokenCredentials;

    public GcpIdTokenService(CloudRunProperties properties) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        if (!(credentials instanceof IdTokenProvider provider)) {
            throw new IllegalStateException(
                    "Las credenciales no soportan ID tokens. "
                            + "Define GOOGLE_APPLICATION_CREDENTIALS con el JSON de una service account.");
        }

        this.idTokenCredentials = IdTokenCredentials.newBuilder()
                .setIdTokenProvider(provider)
                .setTargetAudience(properties.getAudience())
                .build();
    }

    public String getToken() throws IOException {
        idTokenCredentials.refreshIfExpired();
        return idTokenCredentials.getIdToken().getTokenValue();
    }
}
