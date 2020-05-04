package lt.vu.mif.javasnapshot.serialization;

import lt.vu.mif.javasnapshot.Snapshot;

public interface SnapshotSerializer {
    String serialize(Snapshot snapshot);

    SerializerType getType();
}
