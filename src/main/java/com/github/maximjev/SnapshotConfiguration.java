package com.github.maximjev;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.monitorjbl.json.JsonViewModule;
import com.monitorjbl.json.JsonViewSerializer;


public final class SnapshotConfiguration {
    static String JVM_UPDATE_SNAPSHOTS_PARAMETER = "updateSnapshot";

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
        this.compatibility = builder.compatibility;

        this.objectMapper = builder.objectMapper;
        this.prettyPrinter = builder.prettyPrinter;

        SnapshotSerializer snapshotSerializer = new JsonSnapshotSerializer(objectMapper, prettyPrinter);
        this.validator = new SnapshotValidator(snapshotSerializer);

        INSTANCE = this;
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

    PrettyPrinter getPrettyPrinter() {
        return prettyPrinter;
    }

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    StorageType getStorageType() {
        return storageType;
    }

    String getFilePath() {
        return filePath;
    }

    String getFileExtension() {
        return fileExtension;
    }

    boolean isCompatibility() {
        return compatibility;
    }

    public static final class Builder {
        private String filePath = "src/test/java";
        private String fileExtension = "json";
        private boolean compatibility = false;

        private StorageType storageType = StorageType.FLAT_DIRECTORY;

        private ObjectMapper objectMapper = Defaults.INSTANCE.objectMapper();
        private PrettyPrinter prettyPrinter = Defaults.INSTANCE.prettyPrinter();

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
            objectMapper.registerModule(new JsonViewModule(new JsonViewSerializer()));

            if (compatibility) {
                configureCompatible();
            }

            return new SnapshotConfiguration(this);
        }

        private void configureCompatible() {
            this.storageType = StorageType.FLAT_DIRECTORY;
            this.fileExtension = "snap";
            this.objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        }
    }
}
