package lt.vu.mif.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import lt.vu.mif.api.Snapshot;
import lt.vu.mif.model.NonReplacableKeyMap;
import lt.vu.mif.model.TestObject;
import lt.vu.mif.model.TestSubobject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static lt.vu.mif.api.DynamicFields.*;
import static lt.vu.mif.api.FieldMatch.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class JsonSnapshotSerializerTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private JsonSnapshotSerializer serializer = new JsonSnapshotSerializer();


    @Test
    void shouldSerializeBasic() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setStr1("asdf");

        String serialized = serializer.serialize(new Snapshot(ref));
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

        assertNotNull(obj.get("int1"));
        assertEquals(ref.getInt1(), obj.get("int1"));
        assertNotNull(obj.get("str1"));
        assertEquals(ref.getStr1(), obj.get("str1"));
    }

    @Test
    void shouldSerializeList() throws IOException {
        TestObject ref = new TestObject();
        ref.setList(asList("red", "blue", "green"));

        String serialized = serializer.serialize(new Snapshot(ref));
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

        List list = (List) obj.get("list");
        assertEquals(ref.getList().size(), list.size());
        for(int j = 0; j < list.size(); j++) {
            assertEquals(ref.getList().get(j), list.get(j));
        }
    }

    @Test
    void shouldSerializeArray() throws IOException {
        TestObject ref = new TestObject();
        ref.setStringArray(new String[]{"apple", "banana"});

        String serialized = serializer.serialize(new Snapshot(ref));
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

        List array = (List) obj.get("stringArray");
        assertEquals(ref.getStringArray().length, array.size());
        for(int j = 0; j < array.size(); j++) {
            assertEquals(ref.getStringArray()[j], array.get(j));
        }
    }

    @Test
    void shouldSerializeSubObject() throws IOException {
        TestObject ref = new TestObject();
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        String serialized = serializer.serialize(new Snapshot(ref));
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

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

        Snapshot snapshot = new Snapshot(ref)
                .with(dynamicFields(TestObject.class, match().exclude("int1", "str1", "list", "stringArray", "sub")));

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

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

        Snapshot snapshot = new Snapshot(ref)
                .with(dynamicFields(TestObject.class, match().exclude("sub.val", "sub.sub.val")));

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

        Map sub = (Map) obj.get("sub");
        Map subsub = (Map) sub.get("sub");
        assertNotNull(sub);
        assertNotNull(subsub);
        assertNull(sub.get("val"));
        assertNull(subsub.get("val"));
    }

    @Test
    void shouldExcludeAll() throws IOException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setStr1("asdf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot snapshot = new Snapshot(ref)
                .with(dynamicFields(TestObject.class, match().exclude("*")));

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

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

        Snapshot snapshot = new Snapshot(ref)
                .with(dynamicFields(TestObject.class, match().exclude("*").include("list", "str1")));

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

        assertNull(obj.get("int1"));
        assertNotNull(obj.get("str1"));
        assertNotNull(obj.get("list"));
        assertNull(obj.get("stringArray"));
    }

    @Test
    void shouldIncludeNestedFields() throws IOException {
        TestObject ref = new TestObject();
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot snapshot = new Snapshot(ref)
                .with(dynamicFields(TestObject.class, match().exclude("*").include("sub.sub.val")));

        String serialized = serializer.serialize(snapshot);
        Map<String, Object> obj = objectMapper.readValue(serialized, NonReplacableKeyMap.class);

        Map sub = (Map) obj.get("sub");
        Map subsub = (Map) sub.get("sub");
        assertNotNull(sub);
        assertNotNull(subsub);
        assertNull(sub.get("val"));
        assertNotNull(subsub.get("val"));
    }
}
