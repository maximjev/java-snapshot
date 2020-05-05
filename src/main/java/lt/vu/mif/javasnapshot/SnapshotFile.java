package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.exception.SnapshotFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.*;
import static java.util.stream.Collectors.*;

class SnapshotFile {
    private static final String SNAPSHOT_SEPARATOR = "\n\n\n";
    private static final String ENTRY_SEPARATOR = "=";
    private static final String DOT_SEPARATOR = "%s.%s";
    private static final String FILE_EXTENSION = "snap";

    private final String fileName;
    private final String snapshotPrefix;
    private final Path filePath;

    private Map<String, String> snapshots = new HashMap<>();

    private SnapshotFile(Builder builder) {
        this.fileName = resolveFileName(Objects.requireNonNull(builder.name));
        this.filePath = Paths.get(Objects.requireNonNull(builder.path), String.format(DOT_SEPARATOR, fileName, FILE_EXTENSION));
        this.snapshotPrefix = builder.name;

        init();
    }

    private void init() {
        File file = new File(filePath.toUri());
        if (!file.exists()) {
            create(file);
        }
        snapshots = resolveSnapshots(filePath);
        addShutdownHook();
    }

    private void create(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new SnapshotFileException(String.format("Failed to create snapshot file: %s", fileName), e);
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.write(filePath, concatSnapshots().getBytes());
            } catch (IOException e) {
                throw new SnapshotFileException(String.format("Failed to save snapshot file %s:", fileName), e);
            }
        }));
    }

    private Map<String, String> resolveSnapshots(Path filePath) {
        try {
            String content = Files.readString(filePath);
            return Stream.of(content.split(SNAPSHOT_SEPARATOR))
                    .map(String::trim)
                    .map(s -> s.split(ENTRY_SEPARATOR))
                    .filter(s -> s.length == 2)
                    .map((s) -> Map.entry(s[0], s[1]))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new SnapshotFileException(String.format("Failed to parse file %s content", fileName), e);
        }
    }

    private String concatSnapshots() {
        return snapshots.keySet()
                .stream()
                .map(k -> join(ENTRY_SEPARATOR, k, snapshots.get(k)))
                .reduce((s, c) -> join(SNAPSHOT_SEPARATOR, c, s))
                .orElse("");
    }

    public void push(String methodName, String content) {
        snapshots.put(format(methodName), content);
    }

    public boolean exists(String methodName) {
        return snapshots.containsKey(format(methodName));
    }

    public String get(String methodName) {
        return snapshots.get(format(methodName));
    }

    private String format(String methodName) {
        return String.format(DOT_SEPARATOR, snapshotPrefix, methodName);
    }

    private String resolveFileName(String className) {
        String[] tokens = className.split("\\.");
        if (tokens.length == 0) {
            return className;
        }
        return tokens[tokens.length - 1];
    }

    static final class Builder {
        private String path;
        private String name;

        Builder withPath(String path) {
            this.path = path;
            return this;
        }

        Builder withName(String name) {
            this.name = name;
            return this;
        }

        SnapshotFile build() {
            return new SnapshotFile(this);
        }
    }
}
