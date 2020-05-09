package com.github.maximjev;

import java.util.HashMap;
import java.util.Map;

final class DynamicFields {
    private final Map<Class<?>, FieldMatch> matches = new HashMap<>();

    private DynamicFields(Builder builder) {
        this.matches.putAll(builder.matches);
    }

    Map<Class<?>, FieldMatch> getMatches() {
        return matches;
    }

    public final static class Builder {
        private final Snapshot snapshot;
        private final Map<Class<?>, FieldMatch> matches = new HashMap<>();

        Builder(Snapshot snapshot) {
            this.snapshot = snapshot;
        }

        public Builder onClass(Class<?> cls, FieldMatch fieldMatch) {
            matches.put(cls, fieldMatch);
            return this;
        }

        public Snapshot build() {
            return snapshot.withDynamicFields(new DynamicFields(this));
        }
    }
}
