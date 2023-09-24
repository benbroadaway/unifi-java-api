package org.benbroadaway.unifi.actions.usp;

import org.benbroadaway.unifi.UnifiDevice;
import org.benbroadaway.unifi.Usp;
import org.benbroadaway.unifi.actions.ApiResponse;
import org.benbroadaway.unifi.actions.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetRelayStateTest extends AbstractDeviceTest {

    @Test
    void testGetState() {
        HttpResponse<InputStream> device = mock(HttpResponse.class);
        when(device.body()).thenReturn(toInputStream(listOfDevices(3)));
        when(device.statusCode()).thenReturn(200);

        var getAction = new MockGetRelayState(TEST_PLUG, List.of(device));


        var result = assertDoesNotThrow(getAction::call);
        assertNotNull(result);
        assertTrue(result.ok());
        assertTrue(result.data().isPresent());
        assertTrue(result.data().get().get(0).relayState());
    }

    private String listOfDevices(int count) {
        List<UnifiDevice> devices = new ArrayList<>(count);

        Usp base = resourceToObject("device_usp.json", Util.getMapper().constructType(Usp.class));

        for (int i=0; i<count; i++) {
            devices.add(Usp.builder()
                    .from(base)
                    .deviceId("device_" + i)
                    .name("test-usp-" + i)
                    .build());
        }

        var response = ApiResponse.<List<UnifiDevice>>builder().data(devices).build();

        try {
            return Util.withMapper(mapper -> mapper.writeValueAsString(response));
        } catch (IOException e) {
            throw new IllegalStateException("Error serializing mock list of devices");
        }
    }

    private static class MockGetRelayState extends GetRelayState {
        public MockGetRelayState(String plugName, List<HttpResponse<?>> mockResponses) {
            super(plugName, new MockUnifiHttpClient(mockResponses));
        }
    }
}
