package lt.vu.mif.javasnapshot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.monitorjbl.json.JsonViewModule;
import com.monitorjbl.json.JsonViewSerializer;

import java.util.ArrayList;
import java.util.List;

public final class SnapshotConfig {
    private String filePath = "src/test/java";
    private String fileExtension = "snap";

    private static SnapshotConfig INSTANCE;

    private StorageType storageType = StorageType.SAME_DIR;
    private final List<SnapshotStorage> storages = new ArrayList<>();

    private SerializerType serializerType = SerializerType.JSON;
    private final List<SnapshotSerializer> serializers = new ArrayList<>();
    private ObjectMapper objectMapper;
    private PrettyPrinter prettyPrinter;

    private SnapshotValidator validator;

    private SnapshotConfig() {
        objectMapper = objectMapper();
        prettyPrinter = prettyPrinter();

        serializers.add(new JsonSnapshotSerializer(objectMapper, prettyPrinter));
        storages.add(new SameDirSnapshotStorage(filePath, fileExtension));
    }

    SnapshotValidator snapshotValidator() {
        if (validator == null) {
            validator = new SnapshotValidator(serializer(), storage());
        }
        return validator;
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

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JsonViewModule(new JsonViewSerializer()))
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }


    private PrettyPrinter prettyPrinter() {
        DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
        return buildDefaultPrettyPrinter()
                .withArrayIndenter(indenter)
                .withObjectIndenter(indenter);
    }

    private DefaultPrettyPrinter buildDefaultPrettyPrinter() {
        return new DefaultPrettyPrinter("") {
            @Override
            public DefaultPrettyPrinter withSeparators(Separators separators) {
                this._separators = separators;
                this._objectFieldValueSeparatorWithSpaces =
                        separators.getObjectFieldValueSeparator() + " ";
                return this;
            }
        };
    }

    public static SnapshotConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SnapshotConfig();
        }
        return SnapshotConfig.INSTANCE;
    }

    public PrettyPrinter getPrettyPrinter() {
        return prettyPrinter;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public SnapshotConfig withStorageType(StorageType storageType) {
        this.storageType = storageType;
        return this;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public SnapshotConfig withSerializationType(SerializerType serializerType) {
        this.serializerType = serializerType;
        return this;
    }

    public SerializerType getSerializerType() {
        return serializerType;
    }

    public SnapshotConfig withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public SnapshotConfig withFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
