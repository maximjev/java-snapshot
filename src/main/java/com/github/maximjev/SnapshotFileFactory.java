package com.github.maximjev;

final class SnapshotFileFactory {
    SnapshotFile create(String className) {
        SnapshotConfiguration configuration = SnapshotConfiguration.getInstance();
        return configuration.isCompatibility()
                ? createCompatible(className, configuration)
                : createNative(className, configuration);
    }

    private SnapshotFile createCompatible(String className, SnapshotConfiguration configuration) {
        return new CompatibleSnapshotFile.Builder()
                .withName(className)
                .withConfiguration(configuration)
                .build()
                .init();
    }

    private SnapshotFile createNative(String className, SnapshotConfiguration configuration) {
        return new JsonSnapshotFile.Builder()
                .withName(className)
                .withConfiguration(configuration)
                .build()
                .init();
    }
}
