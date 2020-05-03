package lt.vu.mif.config;

import lt.vu.mif.serialization.JsonSnapshotSerializer;
import lt.vu.mif.serialization.SerializerType;
import lt.vu.mif.serialization.SnapshotSerializer;
import lt.vu.mif.service.SnapshotValidator;
import lt.vu.mif.storage.SameDirSnapshotStorage;
import lt.vu.mif.storage.SnapshotStorage;
import lt.vu.mif.storage.StorageType;

import java.util.ArrayList;
import java.util.List;

public class SnapshotConfig {
    private static SnapshotConfig INSTANCE;

    private StorageType storageType = StorageType.SAME_DIR;
    private List<SnapshotStorage> storages = new ArrayList<>();

    private SerializerType serializerType = SerializerType.JSON;
    private List<SnapshotSerializer> serializers = new ArrayList<>();


    private SnapshotConfig() {
        serializers.add(new JsonSnapshotSerializer());
        storages.add(new SameDirSnapshotStorage());
    }

    public static SnapshotConfig getInstance() {
        if (INSTANCE == null) {
             INSTANCE = new SnapshotConfig();
        }
        return SnapshotConfig.INSTANCE;
    }

    SnapshotValidator snapshotValidator() {
        return new SnapshotValidator(serializer(), storage());
    }

    private SnapshotStorage storage() {
        return storages.stream()
                .filter(s -> storageType.equals(s.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(String.format("Storage for type %s is not implemented", storageType)));
    }

    private SnapshotSerializer serializer() {
        return serializers.stream()
                .filter(s -> serializerType.equals(s.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(String.format("Serializer for type %s is not implemented", serializerType)));
    }

    public SnapshotConfig withStorageType(StorageType storageType) {
        SnapshotConfig config = getInstance();
        config.storageType = storageType;
        return config;
    }

    public SnapshotConfig withSerializationType(SerializerType serializerType) {
        SnapshotConfig config = getInstance();
        config.serializerType = serializerType;
        return config;
    }
}
