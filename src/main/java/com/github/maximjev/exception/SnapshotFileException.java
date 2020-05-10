package com.github.maximjev.exception;

public class SnapshotFileException extends RuntimeException {
    public SnapshotFileException(String message) {
        super(message);
    }

    public SnapshotFileException(String message, Throwable cause) {
        super(message, cause);
    }
}