package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.exception.SnapshotMismatchException;

import java.util.HashMap;
import java.util.Map;

final class SnapshotValidator {
    private final Map<String, SnapshotFile> currentFiles = new HashMap<>();
    private final SnapshotSerializer serializer;

    private final SnapshotFileFactory snapshotFileFactory;

    SnapshotValidator(SnapshotSerializer serializer) {
        this.serializer = serializer;
        this.snapshotFileFactory = new SnapshotFileFactory();
    }

    void validate(Snapshot snapshot) {
        String actual = serializer.serialize(snapshot);
        SnapshotFile file = getSnapshotFile(snapshot.getClassName());

        if (file.exists(snapshot)) {
            compare(file.get(snapshot), actual);
        } else {
            file.push(snapshot, actual);
        }
    }

    private SnapshotFile getSnapshotFile(String className) {
        if (currentFiles.containsKey(className)) {
            return currentFiles.get(className);
        }
        SnapshotFile file = snapshotFileFactory.create(className);
        currentFiles.put(className, file);
        return file;
    }

    private void compare(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new SnapshotMismatchException(String.format("expected: %s but was: %s", expected, actual));
        }
    }
}
