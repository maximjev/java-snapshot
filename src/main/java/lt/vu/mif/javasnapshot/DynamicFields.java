package lt.vu.mif.javasnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DynamicFields {
    private final Map<Class<?>, FieldMatch> matches = new HashMap<>();

    private DynamicFields() {
    }

    public DynamicFields and(Class<?> cls, FieldMatch fieldMatch) {
        Objects.requireNonNull(cls);
        Objects.requireNonNull(fieldMatch);
        matches.put(cls, fieldMatch);
        return this;
    }

    Map<Class<?>, FieldMatch> getMatches() {
        return matches;
    }

    public static DynamicFields dynamicFields(Class<?> cls, FieldMatch fieldMatch) {
        return new DynamicFields().and(cls, fieldMatch);
    }
}
