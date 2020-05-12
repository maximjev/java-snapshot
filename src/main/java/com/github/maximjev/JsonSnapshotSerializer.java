package com.github.maximjev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maximjev.exception.SerializationException;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;

import java.util.Map;
import java.util.stream.Stream;


final class JsonSnapshotSerializer implements SnapshotSerializer {

    private final ObjectMapper mapper;
    private final PrettyPrinter prettyPrinter;

    JsonSnapshotSerializer(ObjectMapper mapper, PrettyPrinter prettyPrinter) {
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
                ? resolveMatches(snapshot.getDynamicFields().get().getMatches(), view)
                : view;
    }

    private JsonView<?> resolveMatches(Map<Class<?>, FieldMatch> matches, JsonView<?> view) {
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
}
