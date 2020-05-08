package lt.vu.mif.javasnapshot;

final class SnapshotFileFactory {
    SnapshotFile create(String className) {
        SnapshotConfiguration configuration = SnapshotConfiguration.getInstance();
        if (configuration.isCompatibility()) {
            return new CompatibleSnapshotFile.Builder()
                    .withName(className)
                    .withConfiguration(configuration)
                    .build()
                    .init();
        }
        return new JsonSnapshotFile.Builder()
                .withName(className)
                .withConfiguration(configuration)
                .build()
                .init();
    }
}
