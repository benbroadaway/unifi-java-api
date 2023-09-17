package org.benbroadaway.unifi.exception;

public class UnifiException extends RuntimeException {
    public UnifiException() {
    }

    public UnifiException(String message) {
        super(message);
    }

    public UnifiException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnifiException(Throwable cause) {
        super(cause);
    }

    public UnifiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
