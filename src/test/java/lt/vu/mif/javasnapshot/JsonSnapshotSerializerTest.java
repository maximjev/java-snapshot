package lt.vu.mif.javasnapshot;


import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lt.vu.mif.javasnapshot.Snapshot;
import lt.vu.mif.javasnapshot.SnapshotConfig;
import lt.vu.mif.javasnapshot.JsonSnapshotSerializer;
import lt.vu.mif.javasnapshot.model.NonReplaceableKeyMap;
import lt.vu.mif.javasnapshot.model.TestObject;
import lt.vu.mif.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static lt.vu.mif.javasnapshot.DynamicFields.*;
import static lt.vu.mif.javasnapshot.FieldMatch.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class JsonSnapshotSerializerTest {

    private ObjectMapper mapper;
    private JsonSnapshotSerializer serializer;

    @BeforeEach
    void setup() {
        mapper = SnapshotConfig.getInstance().getObjectMapper();
        serializer = new JsonSnapshotSerializer(mapper, new DefaultPrettyPrinter());
    }

    @Test
    void shouldSerializeBasic() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setStr1("asdf");

        String serialized = serializer.serialize(Snapshot.expect(ref));
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        assertNotNull(obj.get("int1"));
        assertEquals(ref.getInt1(), obj.get("int1"));
        assertNotNull(obj.get("str1"));
        assertEquals(ref.getStr1(), obj.get("str1"));
    }

    @Test
    void shouldSerializeTwoObjects() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setStr1("asdf");

        String serialized = serializer.serialize(Snapshot.expect(ref, ref));
        List<Map<String, Object>> list = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class));

        assertNotNull(list.get(0).get("int1"));
        assertNotNull(list.get(1).get("int1"));
        assertEquals(ref.getInt1(), list.get(0).get("int1"));
        assertEquals(ref.getInt1(), list.get(1).get("int1"));
        assertNotNull(list.get(0).get("str1"));
        assertNotNull(list.get(1).get("str1"));
        assertEquals(ref.getStr1(), list.get(0).get("str1"));
        assertEquals(ref.getStr1(), list.get(1).get("str1"));
    }

    @Test
    void shouldSerializeList() throws IOException {
        TestObject ref = new TestObject();
        ref.setList(asList("red", "blue", "green"));

        String serialized = serializer.serialize(Snapshot.expect(ref));
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        List list = (List) obj.get("list");
        assertEquals(ref.getList().size(), list.size());
        for (int j = 0; j < list.size(); j++) {
            assertEquals(ref.getList().get(j), list.get(j));
        }
    }

    @Test
    void shouldSerializeArray() throws IOException {
        TestObject ref = new TestObject();
        ref.setStringArray(new String[]{"apple", "banana"});

        String serialized = serializer.serialize(Snapshot.expect(ref));
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        List array = (List) obj.get("stringArray");
        assertEquals(ref.getStringArray().length, array.size());
        for (int j = 0; j < array.size(); j++) {
            assertEquals(ref.getStringArray()[j], array.get(j));
        }
    }

    @Test
    void shouldSerializeSubObject() throws IOException {
        TestObject ref = new TestObject();
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        String serialized = serializer.serialize(Snapshot.expect(ref));
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        Map sub = (Map) obj.get("sub");
        Map subsub = (Map) sub.get("sub");
        assertNotNull(sub);
        assertNotNull(subsub);
        assertEquals(ref.getSub().getVal(), sub.get("val"));
        assertEquals(ref.getSub().getSub().getVal(), subsub.get("val"));
    }

    @Test
    void shouldExcludeFields() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setStr1("asdf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot snapshot = Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("int1", "str1", "list", "stringArray", "sub"))
                .build();

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        assertNull(obj.get("int1"));
        assertNull(obj.get("str1"));
        assertNull(obj.get("list"));
        assertNull(obj.get("stringArray"));
        assertNull(obj.get("sub"));
    }

    @Test
    void shouldExcludeNestedFields() throws IOException {
        TestObject ref = new TestObject();
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot snapshot = Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("sub.val", "sub.sub.val"))
                .build();

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        Map sub = (Map) obj.get("sub");
        Map subsub = (Map) sub.get("sub");
        assertNotNull(sub);
        assertNotNull(subsub);
        assertNull(sub.get("val"));
        assertNull(subsub.get("val"));
    }

    @Test
    void shouldExcludeSubobjectMapFields() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setMapOfObjects(ImmutableMap.of(
                "key1", new TestSubobject("test1"),
                "key2", new TestSubobject("test2", new TestSubobject("test3"))
        ));

        Snapshot snapshot = Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestSubobject.class, match().exclude("sub"))
                .build();

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        assertEquals(ref.getInt1(), obj.get("int1"));
        assertTrue(obj.get("mapOfObjects") instanceof Map);
        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) obj.get("mapOfObjects");
        assertEquals(2, map.size());
        assertEquals("test1", map.get("key1").get("val"));
        assertEquals("test2", map.get("key2").get("val"));
        assertNull(map.get("key2").get("sub"));
    }

    @Test
    void shouldExcludeAll() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setStr1("asdf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot snapshot = Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("*"))
                .build();

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        assertNull(obj.get("int1"));
        assertNull(obj.get("str1"));
        assertNull(obj.get("list"));
        assertNull(obj.get("stringArray"));
        assertNull(obj.get("sub"));
    }

    @Test
    void shouldIncludeFields() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setStr1("asdf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});

        Snapshot snapshot = Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("*").include("list", "str1"))
                .build();

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        assertNull(obj.get("int1"));
        assertNotNull(obj.get("str1"));
        assertNotNull(obj.get("list"));
        assertNull(obj.get("stringArray"));
    }

    @Test
    void shouldIncludeNestedFields() throws IOException {
        TestObject ref = new TestObject();
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot snapshot = Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("*").include("sub.sub.val"))
                .build();

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = Arrays.asList(mapper.readValue(serialized, NonReplaceableKeyMap[].class)).get(0);

        Map sub = (Map) obj.get("sub");
        Map subsub = (Map) sub.get("sub");
        assertNotNull(sub);
        assertNotNull(subsub);
        assertNull(sub.get("val"));
        assertNotNull(subsub.get("val"));
    }
}
