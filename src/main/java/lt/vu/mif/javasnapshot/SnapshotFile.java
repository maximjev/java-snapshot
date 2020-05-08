package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.exception.SnapshotFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class SnapshotFile {
    private final String fileName;
    private final Path filePath;

    SnapshotFile(Builder builder) {
        this.fileName = resolveFileName(builder.name);
        this.filePath = Paths.get(builder.configuration.getFilePath(),
                String.format("%s.%s", fileName, builder.configuration.getFileExtension()));
    }

    private String resolveFileName(String className) {
        String[] tokens = className.split("\\.");
        if (tokens.length == 0) {
            return className;
        }
        return tokens[tokens.length - 1];
    }

    SnapshotFile init() {
        File file = new File(filePath.toUri());
        if (!file.exists()) {
            create(file);
        }
        try {
            parseSnapshots(new String(Files.readAllBytes(filePath)));
        } catch (IOException e) {
            throw new SnapshotFileException(String.format("Failed to parse file %s content", fileName), e);
        }

        addShutdownHook();
        return this;
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.write(filePath, saveSnapshots().getBytes());
            } catch (IOException e) {
                throw new SnapshotFileException(String.format("Failed to save snapshot file %s:", fileName), e);
            }
        }));
    }

    protected abstract void parseSnapshots(String fileContent);

    protected abstract String saveSnapshots();

    protected abstract void push(Snapshot snapshot, String content);

    protected abstract boolean exists(Snapshot snapshot);

    protected abstract String get(Snapshot snapshot);

    private void create(File file) {
        try {
            if (!file.canWrite()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            throw new SnapshotFileException(String.format("Failed to create snapshot file: %s", fileName), e);
        }
    }

    static abstract class Builder<T extends SnapshotFile> {
        private String name;
        protected SnapshotConfiguration configuration;

        Builder withName(String name) {
            this.name = name;
            return this;
        }

        Builder withConfiguration(SnapshotConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        abstract T build();
    }
}
