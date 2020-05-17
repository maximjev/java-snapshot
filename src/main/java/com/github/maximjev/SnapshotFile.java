package com.github.maximjev;


import java.nio.file.Path;

abstract class SnapshotFile {
    private final Path filePath;
    protected ObjectMapperWrapper mapper;
    private final FileHandler fileHandler;

    SnapshotFile(Builder builder) {
        this.filePath = builder.filePath;
        this.mapper = builder.objectMapperWrapper;
        this.fileHandler = new FileHandler();
    }

    SnapshotFile init() {
        loadSnapshots(fileHandler.read(filePath));
        fileHandler.saveOnExit(filePath, this::saveSnapshots);
        return this;
    }

    protected abstract void loadSnapshots(String fileContent);

    protected abstract String saveSnapshots();

    protected abstract void push(Snapshot snapshot, String content);

    protected abstract boolean exists(Snapshot snapshot);

    protected abstract String get(Snapshot snapshot);

    static abstract class Builder<T extends SnapshotFile> {
        private Path filePath;
        private ObjectMapperWrapper objectMapperWrapper;

        Builder<T> withFilePath(Path filePath) {
            this.filePath = filePath;
            return this;
        }

        Builder<T> withObjectMapperWrapper(ObjectMapperWrapper objectMapper) {
            this.objectMapperWrapper = objectMapper;
            return this;
        }

        abstract T build();
    }
}
