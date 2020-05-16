package com.github.maximjev;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;


final class CompatibleSnapshotFile extends SnapshotFile {
    private final Map<String, String> snapshots = new LinkedHashMap<>();
    private final CompatibleSnapshotFormatter formatter;

    private CompatibleSnapshotFile(Builder builder) {
        super(builder);
        this.formatter = new CompatibleSnapshotFormatter();
    }

    protected void loadSnapshots(String content) {
        Stream.of(formatter.split(content))
                .map(formatter::match)
                .forEach(this::addSnapshot);
    }

    private void addSnapshot(Matcher matcher) {
        this.snapshots.put(matcher.group("name").trim(), matcher.group("data").trim());
    }

    protected String saveSnapshots() {
        return getAllSnapshots()
                .stream()
                .sorted()
                .reduce(formatter::join)
                .orElse("");
    }

    private List<String> getAllSnapshots() {
        return snapshots.keySet()
                .stream()
                .map(k -> formatter.formatRaw(k, snapshots.get(k)))
                .collect(Collectors.toList());
    }

    protected void push(Snapshot snapshot, String content) {
        snapshots.put(formatter.format(snapshot), content);
    }

    protected boolean exists(Snapshot snapshot) {
        return snapshots.containsKey(formatter.format(snapshot));
    }

    protected String get(Snapshot snapshot) {
        return snapshots.get(formatter.format(snapshot));
    }

    static final class Builder extends SnapshotFile.Builder<CompatibleSnapshotFile> {
        @Override
        CompatibleSnapshotFile build() {
            return new CompatibleSnapshotFile(this);
        }
    }
}
