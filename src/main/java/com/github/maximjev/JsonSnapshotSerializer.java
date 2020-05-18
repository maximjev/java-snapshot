package com.github.maximjev;

import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;

import java.util.Map;
import java.util.stream.Stream;


final class JsonSnapshotSerializer implements SnapshotSerializer {

    private final ObjectMapperWrapper mapper;

    JsonSnapshotSerializer(ObjectMapperWrapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String serialize(Snapshot snapshot) {
        boolean dynamicFields = snapshot.getDynamicFields().isPresent();
        return mapper.write(dynamicFields ? buildView(snapshot) : snapshot.getObject());
    }

    private JsonView<?> buildView(Snapshot snapshot) {
        return Stream
                .of(JsonView.with(snapshot.getObject()))
                .map(view -> dynamicFields(snapshot, view))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(buildError(snapshot)));
    }

    private String buildError(Snapshot snapshot) {
        return String.format("Failed to serialize snapshot: %s", snapshot.getMethodName());
    }

    private JsonView<?> dynamicFields(Snapshot snapshot, JsonView<?> view) {
        return resolveMatches(snapshot.getDynamicFields().get().getMatches(), view);
    }

    private JsonView<?> resolveMatches(Map<Class<?>, FieldMatch> matches, JsonView<?> view) {
        matches.forEach((c, m) -> view.onClass(c, toViewMatch(m)));
        return view;
    }

    private Match toViewMatch(FieldMatch fieldMatch) {
        return Match.match()
                .exclude(fieldMatch.getExcludes().toArray(new String[0]))
                .include(fieldMatch.getIncludes().toArray(new String[0]));
    }
}
