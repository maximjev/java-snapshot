package com.github.maximjev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.maximjev.model.TestObject;
import com.github.maximjev.model.TestSubobject;
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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SuppressWarnings("unchecked")
class JsonSnapshotFileTest {

    private static final String FILE_PATH = "src/test/java/__snapshots__";
    private static final String FILE_EXTENSION = "json";
    private static ObjectMapper mapper;

    @BeforeAll
    static void setup() {
        new SnapshotConfiguration.Builder()
                .withFilePath(FILE_PATH)
                .build();
        mapper = new ObjectMapper();
    }

    @SuppressWarnings("unchecked")
    List<Object> findSnapshot(String methodName, String scenario) {
        return (List<Object>) ((LinkedHashMap<String, Object>) resolveSnapshots().get(format(methodName))).get(scenario);
    }

    @SuppressWarnings("unchecked")
    List<Object> findSnapshot(String methodName) {
        return (List<Object>) resolveSnapshots().get(format(methodName));
    }

    private String format(String methodName) {
        return String.format("%s.%s", getClass().getName(), methodName);
    }

    @SuppressWarnings("unchecked")
    LinkedHashMap<String, Object> resolveSnapshots() {
        try {
            String fileName = String.format("%s.%s", this.getClass().getSimpleName(), FILE_EXTENSION);
            Path path = Paths.get(FILE_PATH, fileName);
            byte[] bytes = Files.readAllBytes(path);
            return mapper.readValue(new String(bytes), LinkedHashMap.class);

        } catch (IOException ignored) {
            return new LinkedHashMap<>();
        }
    }

    @Test
    void shouldUpdateSnapshot() {
        String expected = "expected";
        Snapshot.expect(expected).toUpdate();

        List<Object> list = findSnapshot("shouldUpdateSnapshot");

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(expected, list.get(0));
    }

    @Test
    void shouldMatchStringSnapshot() {
        String expected = "expected";

        Snapshot.expect(expected).toMatchSnapshot();

        List<Object> list = findSnapshot("shouldMatchStringSnapshot");

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
    void shouldThrowMismatchException() {
        Assertions.assertThrows(SnapshotMismatchException.class,
                () -> Snapshot.expect("not expected").toMatchSnapshot()
        );
    }

    @Test
    void shouldResolveFirstCallerMethod() {
        anotherCaller();
    }

    void anotherCaller() {
        String expected = "expected";
        Snapshot.expect(expected).toMatchSnapshot();

        List<Object> list = findSnapshot("shouldResolveFirstCallerMethod");

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0), expected);
    }

    @Test
    void shouldCreateSnapshotWithScenario() {
        String expected = "expected";
        Snapshot.expect(expected).withScenario("scenario").toMatchSnapshot();

        List<Object> list = findSnapshot("shouldResolveFirstCallerMethod");

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
    void shouldCreateScenariosForParameterizedTests(String scenario) {
        String expected = "expected";
        Snapshot.expect(expected).withScenario(scenario).toMatchSnapshot();

        List<Object> list = findSnapshot("shouldCreateScenariosForParameterizedTests", scenario);

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0), expected);
    }

    @Test
    void shouldExcludeDynamicFields() {
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

        List<Object> list = findSnapshot("shouldExcludeDynamicFields");
        Map<String, Object> obj = (Map<String, Object>) list.get(0);

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
    void shouldExcludeWildCard() {
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

        List<Object> list = findSnapshot("shouldExcludeWildCard");
        Map<String, Object> obj = (Map<String, Object>) list.get(0);

        assertNull(obj.get("int1"));
        assertNull(obj.get("date"));
        assertNull(obj.get("str1"));
        assertNull(obj.get("list"));
        assertNull(obj.get("stringArray"));
        assertNull(obj.get("sub"));
    }

    @Test
    void shouldIncludeFields() {
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

        List<Object> list = findSnapshot("shouldIncludeFields");
        Map<String, Object> obj = (Map<String, Object>) list.get(0);

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
