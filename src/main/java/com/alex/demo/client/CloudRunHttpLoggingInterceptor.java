package com.alex.demo.client;

import com.alex.demo.config.ConditionalOnCloudRunAuth;
import com.alex.demo.service.GcpIdTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnCloudRunAuth
public class CloudRunHttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CloudRunHttpLoggingInterceptor.class);

    private final GcpIdTokenService tokenService;

    public CloudRunHttpLoggingInterceptor(GcpIdTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String token;
        try {
            token = tokenService.obtenerTokenAutomatico();
        } catch (Exception e) {
            throw new IOException("No se pudo obtener el ID token de GCP", e);
        }
        request.getHeaders().setBearerAuth(token);

        log.info("=== Cloud Run Request ===");
        log.info("URL: {} {}", request.getMethod(), request.getURI());
        log.info("Token: {}", token);
        if (body.length > 0) {
            log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
        }

        ClientHttpResponse response = execution.execute(request, body);

        byte[] responseBody = response.getBody().readAllBytes();
        log.info("=== Cloud Run Response ===");
        log.info("URL: {}", request.getURI());
        log.info("Status: {}", response.getStatusCode());
        if (responseBody.length > 0) {
            log.info("Response body: {}", new String(responseBody, StandardCharsets.UTF_8));
        } else {
            log.info("Response body: (vacío)");
        }

        return new BufferingClientHttpResponse(response, responseBody);
    }

    private static final class BufferingClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse delegate;
        private final byte[] body;

        private BufferingClientHttpResponse(ClientHttpResponse delegate, byte[] body) {
            this.delegate = delegate;
            this.body = body;
        }

        @Override
        public org.springframework.http.HttpStatusCode getStatusCode() throws IOException {
            return delegate.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return delegate.getStatusText();
        }

        @Override
        public void close() {
            delegate.close();
        }

        @Override
        public java.io.InputStream getBody() {
            return new java.io.ByteArrayInputStream(body);
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }
    }
}
