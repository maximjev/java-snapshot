package com.github.maximjev;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;


final class JsonSnapshotFile extends SnapshotFile {
    private enum ParseType {
        WRITE, READ
    }

    private final Map<String, Object> snapshots = new LinkedHashMap<>();

    private JsonSnapshotFile(Builder builder) {
        super(builder);
    }

    @SuppressWarnings("unchecked")
    protected void loadSnapshots(String content) {
        if (content.isEmpty()) {
            return;
        }
        Map<String, Object> fileContent = (Map<String, Object>) mapper.read(content);
        Object structured = structure(fileContent, ParseType.WRITE);
        this.snapshots.putAll((Map<String, Object>) structured);
    }

    protected String saveSnapshots() {
        Object structured = structure(snapshots, ParseType.READ);
        return mapper.write(structured);
    }

    private Object structure(Map<String, Object> snapshots, ParseType parseType) {
        return snapshots.entrySet()
                .stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        v -> handleValue(v.getValue(), parseType)
                ));
    }

    @SuppressWarnings("unchecked")
    private Object handleValue(Object object, ParseType parseType) {
        if (object instanceof Map) {
            return structure((Map<String, Object>) object, parseType);
        }
        return parseObject(object, parseType);
    }

    @SuppressWarnings("unchecked")
    private Object parseObject(Object object, ParseType parseType) {
        return ParseType.READ.equals(parseType)
                ? mapper.read((String) object)
                : mapper.write(object);
    }

    protected void push(Snapshot snapshot, String content) {
        if (snapshot.getScenario().isPresent()) {
            if (!snapshots.containsKey(format(snapshot))) {
                snapshots.put(format(snapshot), new LinkedHashMap<>());
            }
            Map<String, Object> scenarios = getScenarios(snapshot);
            scenarios.put(snapshot.getScenario().get(), content);
        } else {
            snapshots.put(format(snapshot), content);
        }
    }

    protected boolean exists(Snapshot snapshot) {
        return existsNormal(snapshot) || existsWithScenario(snapshot);
    }

    private boolean existsNormal(Snapshot snapshot) {
        return snapshots.containsKey(format(snapshot)) && !snapshot.getScenario().isPresent();
    }

    private boolean existsWithScenario(Snapshot snapshot) {
        return snapshots.containsKey(format(snapshot))
                && snapshot.getScenario().isPresent()
                && getScenarios(snapshot).containsKey(snapshot.getScenario().get());
    }

    protected String get(Snapshot snapshot) {
        return snapshot.getScenario().isPresent()
                ? (String) getScenarios(snapshot).get(snapshot.getScenario().get())
                : (String) snapshots.get(format(snapshot));
    }

    private String format(Snapshot snapshot) {
        return String.format("%s.%s", snapshot.getClassName(), snapshot.getMethodName());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getScenarios(Snapshot snapshot) {
        return (Map<String, Object>) snapshots.get(format(snapshot));
    }

    static final class Builder extends SnapshotFile.Builder<JsonSnapshotFile> {
        @Override
        JsonSnapshotFile build() {
            return new JsonSnapshotFile(this);
        }
    }
}
