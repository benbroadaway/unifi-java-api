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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SetRelayStateTest extends AbstractDeviceTest {

    @Test
    void testSetState() {
        HttpResponse<InputStream> device = mock(HttpResponse.class);
        when(device.body()).thenReturn(toInputStream(listOfDevices(3)));
        when(device.statusCode()).thenReturn(200);

        HttpResponse<Void> update = mock(HttpResponse.class);
        when(update.statusCode()).thenReturn(200);

        // TODO add set response

        var setAction = new MockSetRelayState(TEST_PLUG, 1, false, List.of(device, update));

        setAction.call();
    }

    private static class MockSetRelayState extends SetRelayState {
        public MockSetRelayState(String plugName, int index, boolean relayState, List<HttpResponse<?>> mockResponses) {
            super(plugName, index, relayState, new MockUnifiHttpClient(mockResponses));
        }
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
}
