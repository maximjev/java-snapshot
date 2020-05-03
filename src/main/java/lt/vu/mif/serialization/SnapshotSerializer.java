package lt.vu.mif.serialization;

import lt.vu.mif.api.Snapshot;

public interface SnapshotSerializer {
    String serialize(Snapshot snapshot);

    SerializerType getType();
}
