package lt.vu.mif.javasnapshot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.*;
import lt.vu.mif.javasnapshot.exception.SerializationException;

import java.util.Map;
import java.util.stream.Stream;


final class JsonSnapshotSerializer implements SnapshotSerializer {

    private final ObjectMapper mapper;
    private final PrettyPrinter prettyPrinter;

    public JsonSnapshotSerializer(ObjectMapper mapper, PrettyPrinter prettyPrinter) {
        this.mapper = mapper;
        this.prettyPrinter = prettyPrinter;
    }

    @Override
    public String serialize(Snapshot snapshot) {
        try {
            return mapper.writer(prettyPrinter)
                    .writeValueAsString(buildView(snapshot));
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize snapshot", e);
        }
    }

    private JsonView<?> buildView(Snapshot snapshot) {
        return Stream
                .of(JsonView.with(snapshot.getObject()))
                .map(view -> dynamicFields(snapshot, view))
                .findFirst()
                .orElseThrow(() -> new SerializationException("Failed to serialize snapshot"));
    }

    private JsonView<?> dynamicFields(Snapshot snapshot, JsonView<?> view) {
        return snapshot.getDynamicFields().isPresent()
                ? resolveMatches(snapshot, view)
                : view;
    }

    private JsonView<?> resolveMatches(Snapshot snapshot, JsonView<?> view) {
        Map<Class<?>, FieldMatch> matches = snapshot.getDynamicFields().get().getMatches();
        if (!matches.isEmpty()) {
            matches.forEach((c, m) -> view.onClass(c, toViewMatch(m)));
        }
        return view;
    }

    private Match toViewMatch(FieldMatch fieldMatch) {
        return Match.match()
                .exclude(fieldMatch.getExcludes().toArray(new String[0]))
                .include(fieldMatch.getIncludes().toArray(new String[0]));
    }

    @Override
    public SerializerType getType() {
        return SerializerType.JSON;
    }
}
