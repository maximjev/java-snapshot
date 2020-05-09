package lt.vu.mif.javasnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.*;

final class CompatibleSnapshotFile extends SnapshotFile {
    public static final String SNAPSHOT_SEPARATOR = "\n\n\n";
    public static final String ENTRY_SEPARATOR = "=";

    private Map<String, String> snapshots = new HashMap<>();

    private CompatibleSnapshotFile(Builder builder) {
        super(builder);
    }

    protected void loadSnapshots(String content) {
        this.snapshots = (Stream.of(content.split(SNAPSHOT_SEPARATOR))
                .map(String::trim)
                .map(s -> s.split(ENTRY_SEPARATOR))
                .filter(s -> s.length == 2)
                .collect(Collectors.toMap(s -> s[0], s -> s[1])));
    }

    protected String saveSnapshots() {
        return snapshots.keySet()
                .stream()
                .map(k -> join(ENTRY_SEPARATOR, k, snapshots.get(k)))
                .reduce((s, c) -> join(SNAPSHOT_SEPARATOR, c, s))
                .orElse("");
    }

    protected void push(Snapshot snapshot, String content) {
        snapshots.put(format(snapshot), content);
    }

    protected boolean exists(Snapshot snapshot) {
        return snapshots.containsKey(format(snapshot));
    }

    protected String get(Snapshot snapshot) {
        return snapshots.get(format(snapshot));
    }

    private String format(Snapshot snapshot) {
        if (snapshot.getScenario().isPresent()) {
            return String.format("%s.%s[%s]", snapshot.getClassName(), snapshot.getMethodName(), snapshot.getScenario().get());
        }
        return String.format("%s.%s", snapshot.getClassName(), snapshot.getMethodName());
    }

    static final class Builder extends SnapshotFile.Builder<CompatibleSnapshotFile> {
        @Override
        CompatibleSnapshotFile build() {
            return new CompatibleSnapshotFile(this);
        }
    }
}
