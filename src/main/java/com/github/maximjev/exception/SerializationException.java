package com.github.maximjev.exception;

public class SerializationException extends RuntimeException {
    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
