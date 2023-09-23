package com.benbroadaway.unifi;

import java.util.Optional;

public interface TaskParams {
    String action();
    String unifiHost();
    boolean validateCerts();
    String username();
    char[] password();

    public interface UspParams extends TaskParams {
        String uspName();
        Optional<Integer> outletIndex();
        boolean relayState();
    }
}
