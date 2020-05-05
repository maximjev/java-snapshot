package lt.vu.mif.javasnapshot;

import java.util.HashMap;
import java.util.Map;

class SameDirSnapshotStorage implements SnapshotStorage {
    private static String DOT_SEPARATOR = "%.%";

    private String filePath;
    private String extension;

    private final Map<String, SnapshotFile> currentFiles = new HashMap<>();

    public SameDirSnapshotStorage(String filePath, String extension) {
        this.filePath = filePath;
        this.extension = extension;
    }

    @Override
    public SnapshotFile get(String name) {
        if (currentFiles.containsKey(name)) {
            return currentFiles.get(name);
        }
        SnapshotFile file = new SnapshotFile
                .Builder()
                .withPath(filePath)
                .withName(name)
                .build();

        currentFiles.put(name, file);
        return file;
    }

    @Override
    public StorageType getType() {
        return StorageType.SAME_DIR;
    }
}
