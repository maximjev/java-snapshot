package lt.vu.mif.javasnapshot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.vu.mif.javasnapshot.exception.SnapshotFileException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


final class JsonSnapshotFile extends SnapshotFile {
    private final ObjectMapper mapper;
    private final PrettyPrinter printer;

    private Map<String, Object> snapshots = new LinkedHashMap<>();

    private JsonSnapshotFile(Builder builder) {
        super(builder);
        this.mapper = builder.configuration.getObjectMapper();
        this.printer = builder.configuration.getPrettyPrinter();
    }

    @SuppressWarnings("unchecked")
    protected void parseSnapshots(String content) {
        if (content.isEmpty()) {
            return;
        }

        this.snapshots.putAll((LinkedHashMap<String, Object>) getObject(content));
    }

    protected String saveSnapshots() {
        return writeObject(this.snapshots);
    }

    protected void push(Snapshot snapshot, String content) {
        Object obj = getObject(content);
        if (snapshot.getScenario().isPresent()) {
            if (!snapshots.containsKey(format(snapshot))) {
                snapshots.put(format(snapshot), new HashMap<String, Object>());
            }
            Map<String, Object> scenarios = getScenarios(snapshot);
            scenarios.put(snapshot.getScenario().get(), obj);
        } else {
            snapshots.put(format(snapshot), obj);
        }
    }

    protected boolean exists(Snapshot snapshot) {
        if (!snapshots.containsKey(format(snapshot)) || !snapshot.getScenario().isPresent()) {
            return false;
        }
        Map<String, Object> scenarios = getScenarios(snapshot);
        return scenarios != null && scenarios.containsKey(snapshot.getScenario().get());
    }

    protected String get(Snapshot snapshot) {
        if (snapshot.getScenario().isPresent()) {
            Map<String, Object> scenarios = getScenarios(snapshot);
            return writeObject(scenarios.get(snapshot.getScenario().get()));
        }
        return writeObject(snapshots.get(format(snapshot)));
    }

    private String format(Snapshot snapshot) {
        return String.format("%s.%s", snapshot.getClassName(), snapshot.getMethodName());
    }

    private Object getObject(String content) {
        try {
            return mapper.readValue(content, Object.class);
        } catch (IOException e) {
            throw new SnapshotFileException(String.format("Failed to parse snapshot %s", content), e);
        }
    }

    private String writeObject(Object obj) {
        try {
            return mapper.writer(printer).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SnapshotFileException(String.format("Failed to parse snapshot %s", obj), e);
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
