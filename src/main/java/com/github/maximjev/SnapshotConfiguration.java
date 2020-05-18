package com.github.maximjev;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class SnapshotConfiguration {
    static String JVM_UPDATE_SNAPSHOTS_PARAMETER = "updateSnapshot";
    static SnapshotConfiguration INSTANCE = new Builder().build();

    private final SnapshotValidator validator;

    private SnapshotConfiguration(Builder builder) {
        ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(
                builder.objectMapper,
                builder.prettyPrinter
        );

        SnapshotFileFactory fileFactory = new SnapshotFileFactory(
                builder.compatibility,
                builder.filePath,
                builder.fileExtension,
                builder.storageType,
                objectMapperWrapper
        );

        SnapshotSerializer snapshotSerializer = new JsonSnapshotSerializer(objectMapperWrapper);
        this.validator = new SnapshotValidator(snapshotSerializer, fileFactory);

        INSTANCE = this;
    }

    SnapshotValidator getSnapshotValidator() {
        return validator;
    }

    public static final class Builder {
        private String filePath = "src/test/java";
        private String fileExtension = "json";
        private boolean compatibility = false;

        private StorageType storageType = StorageType.FLAT_DIRECTORY;

        private ObjectMapper objectMapper = objectMapper();
        private PrettyPrinter prettyPrinter = prettyPrinter();

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
            this.storageType = StorageType.FLAT_DIRECTORY;
            this.fileExtension = "snap";
            this.objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
            return this;
        }

        public SnapshotConfiguration build() {
            return new SnapshotConfiguration(this);
        }

        private ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            return mapper
                    .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .setVisibility(mapper
                            .getSerializationConfig()
                            .getDefaultVisibilityChecker()
                            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
                    );
        }

        private PrettyPrinter prettyPrinter() {
            DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
            return new SnapshotPrettyPrinter()
                    .withArrayIndenter(indenter)
                    .withObjectIndenter(indenter);
        }
    }
}
