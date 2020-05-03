package lt.vu.mif.storage;

public interface SnapshotStorage {
    void store(SnapshotFile file);

    SnapshotFile get(String fileName);

    StorageType getType();
}
