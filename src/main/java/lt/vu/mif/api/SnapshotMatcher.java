package lt.vu.mif.api;

import lt.vu.mif.config.SnapshotConfig;

public class SnapshotMatcher {

    private SnapshotMatcher() {
    }

    public static Snapshot expect(Object object) {
        return new Snapshot(object);
    }

    public static SnapshotConfig configuration() {
        return SnapshotConfig.getInstance();
    }
}
