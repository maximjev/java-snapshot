package com.github.maximjev;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

final class SnapshotValidator {
    private final Map<String, SnapshotFile> currentFiles = new LinkedHashMap<>();
    private final SnapshotSerializer serializer;

    private final SnapshotFileFactory fileFactory;

    SnapshotValidator(SnapshotSerializer serializer, SnapshotFileFactory snapshotFileFactory) {
        this.serializer = serializer;
        this.fileFactory = snapshotFileFactory;
    }

    void validate(Snapshot snapshot) {
        String serialized = serializer.serialize(snapshot);
        SnapshotFile file = getSnapshotFile(snapshot.getClassName());

        if (shouldBeCompared(file, snapshot)) {
            compare(file.get(snapshot), serialized);
        } else {
            file.push(snapshot, serialized);
        }
    }

    void validateInline(Snapshot snapshot, String inline) {
        String serialized = serializer.serialize(snapshot);
        compare(inline.trim(), serialized.trim());
    }

    void update(Snapshot snapshot) {
        String serialized = serializer.serialize(snapshot);
        SnapshotFile file = getSnapshotFile(snapshot.getClassName());
        file.push(snapshot, serialized);
    }

    private boolean shouldBeCompared(SnapshotFile file, Snapshot snapshot) {
        return file.exists(snapshot) && !shouldBeUpdated(snapshot);
    }

    private SnapshotFile getSnapshotFile(String className) {
        return currentFiles.containsKey(className)
                ? currentFiles.get(className)
                : createFile(className);
    }

    private SnapshotFile createFile(String className) {
        SnapshotFile file = fileFactory.create(className);
        currentFiles.put(className, file);
        return file;
    }

    private void compare(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new SnapshotMismatchException(String.format("expected: %s but was: %s", expected, actual));
        }
    }

    private boolean shouldBeUpdated(Snapshot snapshot) {
        return updatePattern().isPresent()
                && snapshot.getClassName().contains(updatePattern().get());
    }

    private Optional<String> updatePattern() {
        return Optional.ofNullable(System.getProperty(SnapshotConfiguration.JVM_UPDATE_SNAPSHOTS_PARAMETER));
    }
}
