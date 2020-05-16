package com.github.maximjev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.JsonViewModule;
import com.monitorjbl.json.JsonViewSerializer;

import java.io.IOException;

class ObjectMapperWrapper {
    private final ObjectMapper mapper;
    private final PrettyPrinter printer;

    public ObjectMapperWrapper(ObjectMapper mapper, PrettyPrinter printer) {
        this.mapper = mapper;
        this.printer = printer;
        this.mapper.registerModule(new JsonViewModule(new JsonViewSerializer()));
    }

    Object read(String content) {
        try {
            return mapper.readValue(content, Object.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to read snapshot %s", content), e);
        }
    }

    String write(Object obj) {
        try {
            return mapper.writer(printer).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(String.format("Failed to write snapshot %s", obj), e);
        }
    }
}
