package com.github.maximjev;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.monitorjbl.json.JsonViewModule;
import com.monitorjbl.json.JsonViewSerializer;

final class Defaults {
    static final Defaults INSTANCE = new Defaults();

    private Defaults() {
    }

    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper
                .registerModule(new JsonViewModule(new JsonViewSerializer()))
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setVisibility(mapper
                        .getSerializationConfig()
                        .getDefaultVisibilityChecker()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    PrettyPrinter prettyPrinter() {
        DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
        return new SnapshotPrettyPrinter()
                .withArrayIndenter(indenter)
                .withObjectIndenter(indenter);
    }

    private static final class SnapshotPrettyPrinter extends DefaultPrettyPrinter {
        @Override
        public DefaultPrettyPrinter withSeparators(Separators separators) {
            this._separators = separators;
            this._objectFieldValueSeparatorWithSpaces =
                    separators.getObjectFieldValueSeparator() + " ";
            return this;
        }
    }
}
