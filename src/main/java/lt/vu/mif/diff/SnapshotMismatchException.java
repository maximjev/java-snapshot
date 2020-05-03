package lt.vu.mif.diff;

public class SnapshotMismatchException extends RuntimeException {
    public SnapshotMismatchException(String message) {
        super(message);
    }

    public SnapshotMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
