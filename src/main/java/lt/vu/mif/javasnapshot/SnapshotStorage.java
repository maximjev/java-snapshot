package lt.vu.mif.javasnapshot;

interface SnapshotStorage {
    SnapshotFile get(String name);

    StorageType getType();
}
