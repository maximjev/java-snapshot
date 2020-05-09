package com.github.javasnapshot;

import com.github.javasnapshot.model.TestObject;
import com.github.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static java.util.Arrays.asList;

public class InlineSnapshotTest {

    @Test
    void shouldMatchInlineStringSnapshot() {
        String expected = "expected";
        Snapshot.expect(expected).toMatchInlineSnapshot("[\n  \"expected\"\n]");
    }

    @Test
    void shouldMatchInlineSnapshot() {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setDate(new Date());
        ref.setStr1("asdssf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot.expect(ref).withDynamicFields()
                .onClass(TestObject.class, FieldMatch.match().exclude("date"))
                .build()
                .toMatchInlineSnapshot("[\n" +
                "  {\n" +
                "    \"sub\": {\n" +
                "      \"val\": \"subvalue\",\n" +
                "      \"sub\": {\n" +
                "        \"val\": \"subsubvalue\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"str1\": \"asdssf\",\n" +
                "    \"int1\": 1,\n" +
                "    \"list\": [\n" +
                "      \"red\",\n" +
                "      \"blue\",\n" +
                "      \"green\"\n" +
                "    ],\n" +
                "    \"stringArray\": [\n" +
                "      \"apple\",\n" +
                "      \"banana\"\n" +
                "    ],\n" +
                "    \"staticValue\": \"TEST\"\n" +
                "  }\n" +
                "]");
    }
}
