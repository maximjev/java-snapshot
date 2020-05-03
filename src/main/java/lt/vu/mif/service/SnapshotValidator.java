package lt.vu.mif.service;

import lt.vu.mif.api.Snapshot;
import lt.vu.mif.serialization.SnapshotSerializer;
import lt.vu.mif.storage.SnapshotStorage;

public class SnapshotValidator {
    private final SnapshotSerializer serializer;
    private final SnapshotStorage storage;

    public SnapshotValidator(SnapshotSerializer serializer, SnapshotStorage storage) {
        this.serializer = serializer;
        this.storage = storage;
    }

    void validate(Snapshot snapshot) {

    }
}
