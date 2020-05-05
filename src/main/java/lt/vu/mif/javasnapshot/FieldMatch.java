package lt.vu.mif.javasnapshot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FieldMatch {
    private final Set<String> includes = new HashSet<>();
    private final Set<String> excludes = new HashSet<>();

    private FieldMatch() {
    }

    public static FieldMatch match() {
        return new FieldMatch();
    }

    public FieldMatch include(String... fields) {
        if (fields != null) {
            this.includes.addAll(Arrays.asList(fields));
        }
        return this;
    }

    public FieldMatch exclude(String... fields) {
        if (fields != null) {
            this.excludes.addAll(Arrays.asList(fields));
        }
        return this;
    }

    Set<String> getIncludes() {
        return includes;
    }

    Set<String> getExcludes() {
        return excludes;
    }
}
