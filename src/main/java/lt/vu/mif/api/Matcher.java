package lt.vu.mif.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Matcher {
    private final Set<String> includes = new HashSet<>();
    private final Set<String> excludes = new HashSet<>();

    private Matcher() {
    }

    public Matcher match() {
        return new Matcher();
    }

    public Matcher include(String... fields) {
        if (fields != null) {
            this.includes.addAll(Arrays.asList(fields));
        }
        return this;
    }

    public Matcher exclude(String... fields) {
        if (fields != null) {
            this.excludes.addAll(Arrays.asList(fields));
        }
        return this;
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }
}
