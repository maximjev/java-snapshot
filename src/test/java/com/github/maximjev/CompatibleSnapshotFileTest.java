package com.github.maximjev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maximjev.model.NonReplaceableKeyMap;
import com.github.maximjev.model.TestObject;
import com.github.maximjev.model.TestSubobject;
import com.monitorjbl.json.JsonViewModule;
import com.monitorjbl.json.JsonViewSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.maximjev.CompatibleSnapshotFormatter.SNAPSHOT_SEPARATOR;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SuppressWarnings("unchecked")
public class CompatibleSnapshotFileTest {
    private static final String FILE_PATH = "src/test/java/__snapshots__";
    private static final String FILE_EXTENSION = "snap";
    private static final String ENTRY_SEPARATOR = "=";
    private static ObjectMapper mapper;

    @BeforeAll
    static void setup() {
        new SnapshotConfiguration.Builder()
                .withFilePath(FILE_PATH)
                .withJsonSnapshotCompatibility()
                .build();
        mapper = new ObjectMapper()
                .registerModule(new JsonViewModule(new JsonViewSerializer()));
    }

    String findSnapshot(String methodName) {
        return resolveSnapshots().get(String.format("%s.%s", getClass().getName(), methodName));
    }

    String findSnapshot(String methodName, String scenario) {
        return resolveSnapshots().get(String.format("%s.%s[%s]", getClass().getName(), methodName, scenario));
    }

    Map<String, String> resolveSnapshots() {
        try {
            String fileName = String.format("%s.%s", this.getClass().getSimpleName(), FILE_EXTENSION);
            Path path = Paths.get(FILE_PATH, fileName);
            byte[] bytes = Files.readAllBytes(path);
            return Stream.of(new String(bytes).split(SNAPSHOT_SEPARATOR))
                    .map(String::trim)
                    .map(s -> s.split(ENTRY_SEPARATOR))
                    .filter(s -> s.length == 2)
                    .collect(Collectors.toMap(s -> s[0], s -> s[1]));
        } catch (IOException ignored) {
            return new HashMap<>();
        }
    }

    @Test
    void shouldUpdateSnapshot() throws JsonProcessingException {
        String expected = "expected";
        Snapshot.expect(expected).toUpdate();

        String snapshot = findSnapshot("shouldUpdateSnapshot");
        List<Object> list = Arrays.asList((Object[]) mapper.readValue(snapshot, String[].class));

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(expected, list.get(0));
    }

    @Test
    void shouldMatchStringSnapshot() throws JsonProcessingException {
        String expected = "expected";

        Snapshot.expect(expected).toMatchSnapshot();

        String snapshot = findSnapshot("shouldMatchStringSnapshot");
        List<Object> list = Arrays.asList((Object[]) mapper.readValue(snapshot, String[].class));

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(expected, list.get(0));
    }

    @Test
    void shouldThrowExceptionOnNull() {
        Assertions.assertThrows(NullPointerException.class,
                () -> Snapshot.expect(null).toMatchSnapshot()
        );
    }

    @Test
    void shouldResolveFirstCallerMethod() throws JsonProcessingException {
        anotherCaller();
    }

    void anotherCaller() throws JsonProcessingException {
        String expected = "expected";
        Snapshot.expect(expected).toMatchSnapshot();

        String snapshot = findSnapshot("shouldResolveFirstCallerMethod");
        List<Object> list = Arrays.asList((Object[]) mapper.readValue(snapshot, String[].class));

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0), expected);
    }

    @Test
    void shouldCreateSnapshotWithScenario() throws JsonProcessingException {
        String expected = "expected";
        Snapshot.expect(expected).withScenario("scenario").toMatchSnapshot();

        String snapshot = findSnapshot("shouldResolveFirstCallerMethod");
        List<Object> list = Arrays.asList((Object[]) mapper.readValue(snapshot, String[].class));

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0), expected);
    }

    @ParameterizedTest(name = "{0} test")
    @ValueSource(strings = {
            "first",
            "second",
            "third",
            "fourth"
    })
    void shouldCreateScenariosForParameterizedTests(String scenario) throws JsonProcessingException {
        String expected = "expected";
        Snapshot.expect(expected).withScenario(scenario).toMatchSnapshot();

        String snapshot = findSnapshot("shouldCreateScenariosForParameterizedTests", scenario);
        List<Object> list = Arrays.asList((Object[]) mapper.readValue(snapshot, String[].class));

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0), expected);
    }

    @Test
    void shouldExcludeDynamicFields() throws JsonProcessingException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setDate(new Date());
        ref.setStr1("asdssf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, FieldMatch.match().exclude("list", "date", "sub.sub"))
                .build()
                .toMatchSnapshot();

        String snapshot = findSnapshot("shouldExcludeDynamicFields");
        Map<String, Object> obj = Arrays.asList(mapper.readValue(snapshot, NonReplaceableKeyMap[].class)).get(0);

        assertNull(obj.get("date"));
        assertNull(obj.get("list"));
        assertNotNull(obj.get("int1"));
        assertEquals(ref.getInt1(), obj.get("int1"));
        assertNotNull(obj.get("str1"));
        assertEquals(ref.getStr1(), obj.get("str1"));
        assertNotNull(obj.get("stringArray"));
        assertEquals(obj.get("stringArray"), Arrays.asList(ref.getStringArray()));
    }

    @Test
    void shouldExcludeWildCard() throws JsonProcessingException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setDate(new Date());
        ref.setStr1("aaaasdsssf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, FieldMatch.match().exclude("*"))
                .build()
                .toMatchSnapshot();

        String snapshot = findSnapshot("shouldExcludeWildCard");
        Map<String, Object> obj = Arrays.asList(mapper.readValue(snapshot, NonReplaceableKeyMap[].class)).get(0);

        assertNull(obj.get("int1"));
        assertNull(obj.get("date"));
        assertNull(obj.get("str1"));
        assertNull(obj.get("list"));
        assertNull(obj.get("stringArray"));
        assertNull(obj.get("sub"));
    }

    @Test
    void shouldIncludeFields() throws JsonProcessingException {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setDate(new Date());
        ref.setStr1("aaaasdsssf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        Snapshot.expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, FieldMatch.match().exclude("*").include("int1", "str1", "list"))
                .build()
                .toMatchSnapshot();

        String snapshot = findSnapshot("shouldIncludeFields");
        Map<String, Object> obj = Arrays.asList(mapper.readValue(snapshot, NonReplaceableKeyMap[].class)).get(0);

        assertNull(obj.get("date"));
        assertNull(obj.get("stringArray"));
        assertNotNull(obj.get("int1"));
        assertEquals(ref.getInt1(), obj.get("int1"));
        assertNotNull(obj.get("str1"));
        assertEquals(ref.getStr1(), obj.get("str1"));
        assertNotNull(obj.get("list"));
        assertEquals(obj.get("list"), ref.getList());
    }
}
