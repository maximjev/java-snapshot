package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.model.TestObject;
import lt.vu.mif.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static java.util.Arrays.asList;
import static lt.vu.mif.javasnapshot.Snapshot.expect;

public class InlineSnapshotTest {

    @Test
    void shouldMatchInlineStringSnapshot() {
        String expected = "expected";
        expect(expected).toMatchInlineSnapshot("[\n" +
                "  \"expected\"\n" +
                "]");
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

        expect(ref).withDynamicFields()
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
