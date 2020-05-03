package lt.vu.mif.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.*;
import lt.vu.mif.api.FieldMatch;
import lt.vu.mif.api.Snapshot;

import java.util.Map;


public class JsonSnapshotSerializer implements SnapshotSerializer {

    private ObjectMapper mapper = new ObjectMapper();

    public JsonSnapshotSerializer() {
        mapper.registerModule(new JsonViewModule(new JsonViewSerializer()));
    }

    @Override
    public String serialize(Snapshot snapshot) {
        try {
            return mapper.writeValueAsString(buildView(snapshot));
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize snapshot", e);
        }
    }

    private JsonView buildView(Snapshot snapshot) {
        JsonView view = JsonView.with(snapshot.getObject());

        if (snapshot.getDynamicFields() != null) {
            Map<Class<?>, FieldMatch> matchers = snapshot.getDynamicFields().getMatches();
            if (!matchers.isEmpty()) {
                matchers.forEach((c, m) -> view.onClass(c, toViewMatch(m)));
            }
        }


        return view;
    }

    private Match toViewMatch(FieldMatch fieldMatch) {
        return Match.match()
                .exclude(fieldMatch.getExcludes().toArray(String[]::new))
                .include(fieldMatch.getIncludes().toArray(String[]::new));
    }

    @Override
    public SerializerType getType() {
        return SerializerType.JSON;
    }
}
