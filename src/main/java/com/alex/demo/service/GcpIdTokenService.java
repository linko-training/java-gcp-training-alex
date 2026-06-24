package com.alex.demo.service;

import com.alex.demo.config.CloudRunProperties;
import com.alex.demo.config.ConditionalOnCloudRunAuth;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdToken;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnCloudRunAuth
public class GcpIdTokenService {

    private final String urlDestino;
    private IdToken cachedIdToken;

    public GcpIdTokenService(CloudRunProperties properties) {
        this.urlDestino = properties.getAudience();
    }

    public String obtenerTokenAutomatico() throws Exception {
        // 1. Esto busca automáticamente la Service Account activa en el entorno de GCP
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        if (credentials instanceof IdTokenProvider provider) {
            if (cachedIdToken == null || cachedIdToken.getExpirationTime().getTime() <= System.currentTimeMillis()) {
                // 2. Genera el token de identidad firmado por la Service Account de forma automática
                cachedIdToken = provider.idTokenWithAudience(urlDestino, List.of());
            }
            return cachedIdToken.getTokenValue();
        }

        throw new RuntimeException("No se pudo obtener el proveedor de tokens automático.");
    }
}
