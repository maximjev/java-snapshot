package com.github.maximjev;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

class FileHandler {

    String read(Path path) {
        File file = new File(path.toUri());
        if (!file.exists()) {
            create(file);
        }
        return loadContent(file);
    }

    void saveOnExit(Path path, Supplier<String> content) {
        SnapshotFileExitHook hook = new SnapshotFileExitHook(path, content);
        Runtime.getRuntime().addShutdownHook(new Thread(hook));
    }

    private void create(File file) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to create snapshot file: %s", file.getName()), e);
        }
    }

    private String loadContent(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to parse file %s content", file.getName()), e);
        }
    }
}
