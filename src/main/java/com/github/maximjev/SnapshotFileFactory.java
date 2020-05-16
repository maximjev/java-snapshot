package com.github.maximjev;

import java.nio.file.Path;
import java.nio.file.Paths;

final class SnapshotFileFactory {
    private final boolean isCompatibility;

    private final String filePath;
    private final String fileExtension;
    private final StorageType storageType;
    private final ObjectMapperWrapper objectMapper;

    public SnapshotFileFactory(boolean isCompatibility,
                               String filePath,
                               String fileExtension,
                               StorageType storageType,
                               ObjectMapperWrapper objectMapper) {
        this.isCompatibility = isCompatibility;
        this.filePath = filePath;
        this.fileExtension = fileExtension;
        this.storageType = storageType;
        this.objectMapper = objectMapper;
    }

    SnapshotFile create(String className) {
        return isCompatibility
                ? createCompatible(className)
                : createNative(className);
    }

    private SnapshotFile createCompatible(String className) {
        return new CompatibleSnapshotFile.Builder()
                .withFilePath(resolveFilePath(className))
                .withObjectMapperWrapper(objectMapper)
                .build()
                .init();
    }

    private SnapshotFile createNative(String className) {
        return new JsonSnapshotFile.Builder()
                .withFilePath(resolveFilePath(className))
                .withObjectMapperWrapper(objectMapper)
                .build()
                .init();
    }

    private Path resolveFilePath(String className) {
        return storageType.equals(StorageType.BY_PACKAGE_HIERARCHY)
                ? constructPackagePath(filePath, className)
                : Paths.get(filePath, resolveFileName(className));
    }

    private Path constructPackagePath(String filePath, String className) {
        String[] packagePath = className.split("\\.");
        packagePath[packagePath.length - 1] = resolveFileName(className);
        return Paths.get(filePath, packagePath);
    }

    private String resolveFileName(String className) {
        String[] tokens = className.split("\\.");
        return String.format("%s.%s", tokens[tokens.length - 1], fileExtension);
    }
}
