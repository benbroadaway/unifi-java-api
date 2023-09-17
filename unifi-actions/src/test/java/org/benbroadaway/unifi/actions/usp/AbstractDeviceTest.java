package org.benbroadaway.unifi.actions.usp;

import com.fasterxml.jackson.databind.JavaType;
import org.benbroadaway.unifi.actions.UnifiHttpClient;
import org.benbroadaway.unifi.actions.Util;
import org.benbroadaway.unifi.client.ApiCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

public abstract class AbstractDeviceTest {

    protected static final String TEST_HOST = "https://localhost:123";
    protected static final String TEST_PLUG = "test-usp-1";
    protected static final ApiCredentials TEST_CREDS = ApiCredentials.getInstance("test", "test".toCharArray());


    protected static <T> T resourceToObject(String resource, JavaType t) {
        return resourceToInputStream(resource, is -> {
            try {
                return Util.withMapper(m -> m.readValue(is, t));
            } catch (IOException e) {
                throw new IllegalStateException("Error parsing resource to object", e);
            }
        });
    }

    protected static <T> T resourceToInputStream(String resource, Function<InputStream, T> f) {
        try (var is = GetRelayState.class.getClassLoader().getResourceAsStream(resource)) {
            return f.apply(is);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading resource", e);
        }
    }

    protected InputStream toInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }

    protected static class MockUnifiHttpClient extends UnifiHttpClient {
        private final Queue<HttpResponse<?>> responses;

        public MockUnifiHttpClient(List<HttpResponse<?>> responses) {
            super(TEST_HOST, TEST_CREDS, true);
            this.responses = new ArrayDeque<>(responses);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> HttpResponse<T> send(HttpRequest req, HttpResponse.BodyHandler<T> handler) {
            if (responses.isEmpty()) {
                throw new IllegalStateException("not enough mock responses for test");
            }

            return (HttpResponse<T>) responses.poll();
        }

        @Override
        protected String getCsrfToken(URI host, ApiCredentials creds, HttpClient client) {
            return "test-csrf-token";
        }
    }
}
