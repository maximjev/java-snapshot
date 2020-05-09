package com.github.maximjev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maximjev.exception.SnapshotFileException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;


final class JsonSnapshotFile extends SnapshotFile {
    private enum ParseType {
        WRITE, READ
    }

    private final ObjectMapper mapper;
    private final PrettyPrinter printer;

    private Map<String, Object> snapshots = new LinkedHashMap<>();

    private JsonSnapshotFile(Builder builder) {
        super(builder);
        this.mapper = builder.configuration.getObjectMapper();
        this.printer = builder.configuration.getPrettyPrinter();
    }

    @SuppressWarnings("unchecked")
    protected void loadSnapshots(String content) {
        if (content.isEmpty()) {
            return;
        }
        Map<String, Object> fileContent = (Map<String, Object>) read(content);
        Object structured = structure(fileContent, ParseType.WRITE);
        this.snapshots.putAll((Map<String, Object>) structured);
    }

    protected String saveSnapshots() {
        Object structured = structure(snapshots, ParseType.READ);
        return write(structured);
    }

    private Object structure(Map<String, Object> snapshots, ParseType parseType) {
        return snapshots.entrySet()
                .stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        v -> parseObject(v.getValue(), parseType)
                ));
    }

    @SuppressWarnings("unchecked")
    private Object parseObject(Object object, ParseType parseType) {
        if (object instanceof Map) {
            return structure((Map<String, Object>) object, parseType);
        }
        return ParseType.READ.equals(parseType)
                ? read((String) object)
                : write(object);
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
        if (snapshots.containsKey(format(snapshot)) && !snapshot.getScenario().isPresent()) {
            return true;
        }
        Map<String, Object> scenarios = getScenarios(snapshot);
        return scenarios != null && scenarios.containsKey(snapshot.getScenario().get());
    }

    protected String get(Snapshot snapshot) {
        if (snapshot.getScenario().isPresent()) {
            Map<String, Object> scenarios = getScenarios(snapshot);
            return (String) scenarios.get(snapshot.getScenario().get());
        }
        return (String) snapshots.get(format(snapshot));
    }

    private String format(Snapshot snapshot) {
        return String.format("%s.%s", snapshot.getClassName(), snapshot.getMethodName());
    }

    private Object read(String content) {
        try {
            return mapper.readValue(content, Object.class);
        } catch (IOException e) {
            throw new SnapshotFileException(String.format("Failed to read snapshot %s", content), e);
        }
    }

    private String write(Object obj) {
        try {
            return mapper.writer(printer).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SnapshotFileException(String.format("Failed to write snapshot %s", obj), e);
        }
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
