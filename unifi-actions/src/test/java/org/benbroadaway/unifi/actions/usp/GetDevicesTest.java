package org.benbroadaway.unifi.actions.usp;

import org.benbroadaway.unifi.UnifiDevice;
import org.benbroadaway.unifi.UnknownDevice;
import org.benbroadaway.unifi.Usp;
import org.benbroadaway.unifi.actions.ApiResponse;
import org.benbroadaway.unifi.actions.Util;
import org.benbroadaway.unifi.actions.device.GetDevices;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDevicesTest extends AbstractDeviceTest {

    @Test
    void testGetDevices() {
        HttpResponse<InputStream> devices = mock(HttpResponse.class);
        when(devices.body()).thenReturn(toInputStream(listOfDevices()));
        when(devices.statusCode()).thenReturn(200);

        var getAction = new MockGetDevices(List.of(devices));

        var result = assertDoesNotThrow(getAction::call);
        assertNotNull(result);
        assertTrue(result.ok());
        assertTrue(result.data().isPresent());
        var resultDevices = result.data().get();
        assertEquals(2, resultDevices.size());
        assertTrue(resultDevices.get(0) instanceof Usp);
        assertTrue(resultDevices.get(1) instanceof UnknownDevice);
    }

    private static String listOfDevices() {
        var devices = new LinkedList<UnifiDevice>();
        Usp baseUsp = resourceToObject("device_usp.json", Util.getMapper().constructType(Usp.class));

        devices.add(Usp.builder()
                .from(baseUsp)
                .deviceId("device_0")
                .name("test-usp-1")
                .build());
        devices.add(UnknownDevice.copyOf(baseUsp)
                .deviceId("device_1")
                .name("test-unknown-device-1")
                .model("unknown")
                .build());

        var response = ApiResponse.<List<UnifiDevice>>builder().data(devices).build();

        try {
            return Util.withMapper(mapper -> mapper.writeValueAsString(response));
        } catch (IOException e) {
            throw new IllegalStateException("Error serializing mock list of devices", e);
        }
    }

    private static class MockGetDevices extends GetDevices {
        public MockGetDevices(List<HttpResponse<?>> mockResponses) {
            super(new MockUnifiHttpClient(mockResponses));
        }
    }
}
