package lt.vu.mif.storage;

public class SameDirSnapshotStorage implements SnapshotStorage {

    @Override
    public void store(SnapshotFile file) {

    }

    @Override
    public SnapshotFile get(String fileName) {
        return null;
    }

    @Override
    public StorageType getType() {
        return StorageType.SAME_DIR;
    }
}
