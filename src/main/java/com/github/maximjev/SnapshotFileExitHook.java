package com.github.maximjev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class SnapshotFileExitHook implements Runnable {
    private final Path filePath;
    private final String data;

    public SnapshotFileExitHook(Path filePath, String data) {
        this.filePath = filePath;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            Files.write(filePath, data.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to save snapshot file %s:", filePath.getFileName()), e);
        }
    }
}
