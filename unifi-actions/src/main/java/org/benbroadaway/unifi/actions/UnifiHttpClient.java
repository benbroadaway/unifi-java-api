package org.benbroadaway.unifi.actions;

import org.benbroadaway.unifi.client.ApiCredentials;
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
import java.util.function.BiFunction;

public class UnifiHttpClient {
    private static final Logger log = LoggerFactory.getLogger(UnifiHttpClient.class);

    private final HttpClient client;
    private String token;
    private final URI unifiHost;

    public static final String APPLICATION_JSON = "application/json";

    public UnifiHttpClient(String unifiHost, ApiCredentials credentials, boolean validateCerts) {
        this.unifiHost = toUri(unifiHost);
        this.client = validateCerts ? defaultClient() : nonValidatingClient();

        try {
            this.token = initializeAuth(this.unifiHost, credentials, this.client);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize authentication token: " + e.getMessage());
        }
    }

    public URI resolve(String uriPath) {
        return unifiHost.resolve(uriPath);
    }

    public <T> T withClient(BiFunction<HttpClient, String, T> f) {
        return f.apply(client, token);
    }

    private static URI toUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid uri given: " + e.getMessage());
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
                    .orElseThrow(() -> new IllegalStateException("Successful auth call, but no csrf token found"));
            case 403 -> throw new IllegalStateException("(403) Invalid auth");
            default -> throw new IllegalStateException("(" + resp.statusCode() + "') Error calling auth endpoint");
        };
    }

    private static HttpClient defaultClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .build();
    }

    private static HttpClient nonValidatingClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
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
            throw new RuntimeException("Error initializing SSLContext: " + e.getMessage());
        }
    }
}
