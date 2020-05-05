package lt.vu.mif.javasnapshot;

interface SnapshotSerializer {
    String serialize(Snapshot snapshot);

    SerializerType getType();
}
