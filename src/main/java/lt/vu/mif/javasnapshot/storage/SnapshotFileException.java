package lt.vu.mif.javasnapshot.storage;

public class SnapshotFileException extends RuntimeException {
    public SnapshotFileException(String message) {
        super(message);
    }

    public SnapshotFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
