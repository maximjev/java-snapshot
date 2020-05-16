package com.github.maximjev;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.String.join;

final class CompatibleSnapshotFile extends SnapshotFile {
    public static final String SNAPSHOT_SEPARATOR = "\n\n\n";
    public static final String ENTRY_SEPARATOR = "=";

    private static final int REGEX_FLAGS = Pattern.MULTILINE + Pattern.DOTALL;
    private static final Pattern REGEX =
            Pattern.compile("(?<name>[^ =]*) *=+ *(?<data>\\[.*\\])[^\\]]*", REGEX_FLAGS);

    private final Map<String, String> snapshots = new LinkedHashMap<>();

    private CompatibleSnapshotFile(Builder builder) {
        super(builder);
    }

    protected void loadSnapshots(String content) {
        Stream.of(content.split(SNAPSHOT_SEPARATOR))
                .filter(String::isEmpty)
                .map(String::trim)
                .forEach(this::addSnapshot);
    }

    private void addSnapshot(String raw) {
        Matcher matcher = REGEX.matcher(raw);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Raw data string does not match expected pattern. String: " + raw);
        }
        this.snapshots.put(matcher.group("name").trim(), matcher.group("data").trim());
    }

    protected String saveSnapshots() {
        return snapshots.keySet()
                .stream()
                .map(k -> raw(k, snapshots.get(k)))
                .sorted()
                .reduce(this::joinSnapshots)
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

    private String raw(String name, String data) {
        return String.join("=", name, data);
    }

    private String joinSnapshots(String entry, String another) {
        return join(SNAPSHOT_SEPARATOR, entry, another);
    }

    static final class Builder extends SnapshotFile.Builder<CompatibleSnapshotFile> {
        @Override
        CompatibleSnapshotFile build() {
            return new CompatibleSnapshotFile(this);
        }
    }
}
