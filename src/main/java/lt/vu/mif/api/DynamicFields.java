package lt.vu.mif.api;

import java.util.HashMap;
import java.util.Map;

public class DynamicFields {
    private final Map<Class<?>, Matcher> matches = new HashMap<>();

    public DynamicFields onClass(Class<?> cls, Matcher matcher) {
        matches.put(cls, matcher);
        return this;
    }

    public Map<Class<?>, Matcher> getMatches() {
        return matches;
    }
}
