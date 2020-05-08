package lt.vu.mif.javasnapshot;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.JsonViewModule;
import com.monitorjbl.json.JsonViewSerializer;

import java.util.Objects;

public final class SnapshotConfiguration {
    private static SnapshotConfiguration INSTANCE;

    private final String filePath;
    private final String fileExtension;
    private final StorageType storageType;

    private final boolean compatibility;


    private final ObjectMapper objectMapper;
    private final PrettyPrinter prettyPrinter;

    private final SnapshotValidator validator;

    private SnapshotConfiguration(Builder builder) {
        this.filePath = builder.filePath;
        this.fileExtension = builder.fileExtension;
        this.storageType = builder.storageType;

        this.objectMapper = builder.objectMapper;
        this.prettyPrinter = builder.prettyPrinter;

        this.compatibility = builder.compatibility;

        this.validator = new SnapshotValidator(new JsonSnapshotSerializer(objectMapper, prettyPrinter));

        INSTANCE = this;
    }

    private static Defaults getDefaults() {
        return Defaults.INSTANCE;
    }

    public static SnapshotConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Builder().build();
        }
        return SnapshotConfiguration.INSTANCE;
    }

    SnapshotValidator getSnapshotValidator() {
        return validator;
    }

    public PrettyPrinter getPrettyPrinter() {
        return prettyPrinter;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public boolean isCompatibility() {
        return compatibility;
    }

    public static final class Builder {
        private String filePath = "src/test/java";
        private String fileExtension = "json";
        private boolean compatibility = false;

        private StorageType storageType = StorageType.FLAT_DIRECTORY;

        private ObjectMapper objectMapper;
        private PrettyPrinter prettyPrinter;

        public Builder() {
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public Builder withStorageType(StorageType storageType) {
            this.storageType = storageType;
            return this;
        }

        public Builder withObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder withPrettyPrinter(PrettyPrinter prettyPrinter) {
            this.prettyPrinter = prettyPrinter;
            return this;
        }

        public Builder withJsonSnapshotCompatibility() {
            this.compatibility = true;
            return this;
        }

        public SnapshotConfiguration build() {
            Objects.requireNonNull(filePath);
            Objects.requireNonNull(fileExtension);
            Objects.requireNonNull(storageType);

            final Defaults defaults = getDefaults();

            if (objectMapper == null) {
                objectMapper = defaults.objectMapper();
            }
            objectMapper.registerModule(new JsonViewModule(new JsonViewSerializer()));

            if (prettyPrinter == null) {
                prettyPrinter = defaults.prettyPrinter();
            }
            if (compatibility) {
                this.fileExtension = "snap";
            }

            return new SnapshotConfiguration(this);
        }
    }
}
