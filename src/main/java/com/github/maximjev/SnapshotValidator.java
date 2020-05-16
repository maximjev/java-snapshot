package com.github.maximjev;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

final class SnapshotValidator {
    private final Map<String, SnapshotFile> currentFiles = new LinkedHashMap<>();
    private final SnapshotSerializer serializer;

    private final SnapshotFileFactory snapshotFileFactory;

    SnapshotValidator(SnapshotSerializer serializer) {
        this.serializer = serializer;
        this.snapshotFileFactory = new SnapshotFileFactory();
    }

    void validate(Snapshot snapshot) {
        String serialized = serializer.serialize(snapshot);
        SnapshotFile file = getSnapshotFile(snapshot.getClassName());

        if (shouldBeUpdated(snapshot, file)) {
            file.push(snapshot, serialized);
            return;
        }

        if (file.exists(snapshot)) {
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

    private boolean shouldBeUpdated(Snapshot snapshot, SnapshotFile file) {
        return updatePattern().isPresent()
                && snapshot.getClassName().contains(updatePattern().get())
                && file.exists(snapshot);
    }

    private Optional<String> updatePattern() {
        return Optional.ofNullable(System.getProperty(SnapshotConfiguration.JVM_UPDATE_SNAPSHOTS_PARAMETER));
    }
}
