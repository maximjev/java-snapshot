package lt.vu.mif.api;

import java.util.HashMap;
import java.util.Map;

public class DynamicFields {
    private final Map<Class<?>, FieldMatch> matches = new HashMap<>();

    private DynamicFields() {
    }

    public DynamicFields and(Class<?> cls, FieldMatch fieldMatch) {
        matches.put(cls, fieldMatch);
        return this;
    }

    public Map<Class<?>, FieldMatch> getMatches() {
        return matches;
    }

    public static DynamicFields dynamicFields(Class<?> cls, FieldMatch fieldMatch) {
        return new DynamicFields().and(cls, fieldMatch);
    }
}
