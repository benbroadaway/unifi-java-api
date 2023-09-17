package org.benbroadaway.unifi.actions;

import org.benbroadaway.unifi.client.ApiCredentials;
import org.benbroadaway.unifi.exception.UnifiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

public class UnifiHttpClient {
    private static final Logger log = LoggerFactory.getLogger(UnifiHttpClient.class);

    private final HttpClient client;
    private final String token;
    private final URI unifiHost;

    public static final String APPLICATION_JSON = "application/json";

    public UnifiHttpClient(String unifiHost, ApiCredentials credentials, boolean validateCerts) {
        this.unifiHost = toUri(unifiHost);
        this.client = validateCerts ? defaultClient() : nonValidatingClient();
        this.token = getCsrfToken(this.unifiHost, credentials, this.client);
    }

    protected String getCsrfToken(URI host, ApiCredentials creds, HttpClient client) {
        try {
            return initializeAuth(host, creds, client);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UnifiException(e);
        } catch (Exception e) {
            log.error("Error getting token from {}", host);
            throw new IllegalStateException("Failed to initialize authentication token: " + e.getMessage());
        }
    }

    public URI resolve(String uriPath) {
        return unifiHost.resolve(uriPath);
    }

    public String[] getCsrfHeader() {
        return List.of("x-csrf-token", token).toArray(new String[0]);
    }

    public <T> HttpResponse<T> send(HttpRequest req, HttpResponse.BodyHandler<T> handler) throws IOException, InterruptedException {
        return client.send(req, handler);
    }

    private static URI toUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new UnifiException("Invalid uri given: " + e.getMessage());
        }
    }

    private static String initializeAuth(URI host, ApiCredentials creds, HttpClient client) throws IOException, InterruptedException {
        var body = Util.withMapper(mapper -> mapper.writeValueAsString(creds));

        var req = HttpRequest.newBuilder()
                .uri(host.resolve("/api/auth/login"))
                .header("Content-Type", UnifiHttpClient.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        var resp = client.send(req, HttpResponse.BodyHandlers.discarding());

        return switch (resp.statusCode()) {
            case 200 -> resp.headers()
                    .firstValue("x-csrf-token")
                    .orElseThrow(() -> new UnifiException("Successful auth call, but no csrf token found"));
            case 403 -> throw new UnifiException("(403) Invalid auth");
            default -> throw new UnifiException("(" + resp.statusCode() + "') Error calling auth endpoint");
        };
    }

    private static HttpClient defaultClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private static HttpClient nonValidatingClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .connectTimeout(Duration.ofSeconds(10))
                .sslContext(nonValidatingSSLContext())
                .build();
    }

    private static SSLContext nonValidatingSSLContext() {
        log.warn("Using HTTP client with no SSL validation.");
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");

            TrustManager[] managers = new TrustManager[1];
            managers[0] = new NoopTrustManager();

            ctx.init(null, managers, new SecureRandom());

            return ctx;
        } catch (Exception e) {
            throw new UnifiException("Error initializing SSLContext: " + e.getMessage());
        }
    }
}
