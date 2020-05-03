package lt.vu.mif.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.*;
import lt.vu.mif.api.Matcher;
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

        Map<Class<?>, Matcher> matchers = snapshot.getDynamicFields().getMatches();

        if (!matchers.isEmpty()) {
            matchers.forEach((c, m) -> view.onClass(c, toViewMatch(m)));
        }

        return view;
    }

    private Match toViewMatch(Matcher matcher) {
        return Match.match()
                .exclude(matcher.getExcludes().toArray(String[]::new))
                .include(matcher.getIncludes().toArray(String[]::new));
    }

    @Override
    public SerializerType getType() {
        return SerializerType.JSON;
    }
}
