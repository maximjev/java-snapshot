package com.github.maximjev;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class FieldMatch {
    private final Set<String> includes = new HashSet<>();
    private final Set<String> excludes = new HashSet<>();

    private FieldMatch() {
    }

    public static FieldMatch match() {
        return new FieldMatch();
    }

    public FieldMatch include(String... fields) {
        this.includes.addAll(Arrays.asList(Objects.requireNonNull(fields)));
        return this;
    }

    public FieldMatch exclude(String... fields) {
        this.excludes.addAll(Arrays.asList(Objects.requireNonNull(fields)));
        return this;
    }

    Set<String> getIncludes() {
        return includes;
    }

    Set<String> getExcludes() {
        return excludes;
    }
}
