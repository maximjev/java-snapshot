package com.github.maximjev;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class SnapshotFile {
    private final String fileName;
    private final Path filePath;

    SnapshotFile(Builder builder) {
        SnapshotConfiguration config = builder.configuration;
        this.fileName = resolveFileName(builder.name, config.getFileExtension());

        if (config.getStorageType().equals(StorageType.BY_PACKAGE_HIERARCHY)) {
            this.filePath = constructPackagePath(config.getFilePath(), builder.name);
        } else {
            this.filePath = Paths.get(config.getFilePath(), fileName);
        }
    }

    private Path constructPackagePath(String filePath, String name) {
        String[] packagePath = name.split("\\.");
        packagePath[packagePath.length - 1] = fileName;
        return Paths.get(filePath, packagePath);
    }

    private String resolveFileName(String className, String extension) {
        String[] tokens = className.split("\\.");
        return String.format("%s.%s", tokens[tokens.length - 1], extension);
    }

    SnapshotFile init() {
        File file = new File(filePath.toUri());
        if (!file.exists()) {
            create(file);
        }

        initSnapshots();
        addShutdownHook();
        return this;
    }

    private void initSnapshots() {
        try {
            loadSnapshots(new String(Files.readAllBytes(filePath)));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to parse file %s content", fileName), e);
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.write(filePath, saveSnapshots().getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Failed to save snapshot file %s:", fileName), e);
            }
        }));
    }

    protected abstract void loadSnapshots(String fileContent);

    protected abstract String saveSnapshots();

    protected abstract void push(Snapshot snapshot, String content);

    protected abstract boolean exists(Snapshot snapshot);

    protected abstract String get(Snapshot snapshot);

    private void create(File file) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to create snapshot file: %s", fileName), e);
        }
    }

    static abstract class Builder<T extends SnapshotFile> {
        private String name;
        protected SnapshotConfiguration configuration;

        Builder<T> withName(String name) {
            this.name = name;
            return this;
        }

        Builder<T> withConfiguration(SnapshotConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        abstract T build();
    }
}
