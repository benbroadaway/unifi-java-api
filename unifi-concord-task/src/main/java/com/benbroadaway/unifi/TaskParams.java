package com.benbroadaway.unifi;

public interface TaskParams {
    String action();
    String unifiHost();
    boolean validateCerts();
    String username();
    char[] password();

    public interface UspParams extends TaskParams {
        String uspName();
        boolean relayState();
    }
}
