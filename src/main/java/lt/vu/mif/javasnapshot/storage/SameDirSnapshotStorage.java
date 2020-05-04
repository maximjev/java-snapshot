package lt.vu.mif.javasnapshot.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SameDirSnapshotStorage implements SnapshotStorage {
    private String filePath;
    private String extension;

    private final Map<String, SnapshotFile> currentFiles = new HashMap<>();

    public SameDirSnapshotStorage(String filePath, String extension) {
        this.filePath = filePath;
        this.extension = extension;
    }

    @Override
    public void store(SnapshotFile file) {

    }

    @Override
    public SnapshotFile get(String className) {
        if (currentFiles.containsKey(className)) {
            return currentFiles.get(className);
        }
        SnapshotFile file = new SnapshotFile
                .Builder()
                .withPath(filePath)
                .withClassName(className)
                .build();

        currentFiles.put(className, file);
        return file;
    }

    @Override
    public StorageType getType() {
        return StorageType.SAME_DIR;
    }
}
