package org.benbroadaway.unifi.actions;

import com.fasterxml.jackson.databind.JavaType;
import org.benbroadaway.unifi.UnifiDevice;
import org.benbroadaway.unifi.Usp;
import org.benbroadaway.unifi.client.ApiCredentials;
import org.benbroadaway.unifi.exception.UnifiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public abstract class AbstractAction {

    private final UnifiHttpClient unifiClient;

    protected AbstractAction(String host, ApiCredentials creds, boolean validateCerts) {
        this.unifiClient = new UnifiHttpClient(host, creds, validateCerts);
    }

    protected AbstractAction(UnifiHttpClient unifiClient) {
        this.unifiClient = unifiClient;
    }

    protected UnifiHttpClient getUnifiClient() {
        return this.unifiClient;
    }

    protected List<UnifiDevice> getDevices() {
        var uri = unifiClient.resolve("/proxy/network/api/s/default/stat/device");

        var req = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", UnifiHttpClient.APPLICATION_JSON)
                .headers(unifiClient.getCsrfHeader())
                .build();

        try {
            var resp = unifiClient.send(req, HttpResponse.BodyHandlers.ofInputStream());

            if (resp.statusCode() != 200) {
                throw new IllegalStateException("invalid response code: ${resp.statusCode()}");
            }

            var listOfDevicesType = Util.getMapper().getTypeFactory().constructCollectionType(List.class, UnifiDevice.class);

            ApiResponse<List<UnifiDevice>> apiResponse = readBody(resp, listOfDevicesType);

            return apiResponse.data();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UnifiException(e);
        } catch (Exception e) {
            throw new UnifiException("Error retrieving devices: " + e.getMessage());
        }
    }

    protected Usp getCurrentDevice(String model, String name) {
        return getDevices().stream()
                .filter(d -> d.model().equals(model))
                .filter(d -> d.name().equals(name))
                .map(Usp.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No '" + model + "' device found with name '" + name + "'"));
    }

    protected <T> ApiResponse<T> readBody(HttpResponse<InputStream> resp, JavaType returnParam) {
        try (var input = resp.body()) {
            return Util.withMapper(mapper -> {
                var t = mapper.getTypeFactory().constructParametricType(ApiResponse.class, returnParam);
                return mapper.readValue(input, t);
            });
        } catch (IOException e) {
            throw new UnifiException(e);
        }
    }
}
