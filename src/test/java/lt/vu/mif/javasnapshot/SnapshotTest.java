package lt.vu.mif.javasnapshot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lt.vu.mif.javasnapshot.SnapshotConfiguration;
import lt.vu.mif.javasnapshot.model.NonReplaceableKeyMap;
import lt.vu.mif.javasnapshot.model.TestObject;
import lt.vu.mif.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static lt.vu.mif.javasnapshot.Snapshot.expect;
import static lt.vu.mif.javasnapshot.FieldMatch.match;
import static lt.vu.mif.javasnapshot.SnapshotFile.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class SnapshotTest {
    private static final String FILE_PATH = "src/test/java/__snapshots__";
    private static SnapshotConfiguration configuration;
    private static ObjectMapper mapper;

    @BeforeAll
    static void setup() {
        configuration = new SnapshotConfiguration.Builder()
                .withFilePath(FILE_PATH)
                .build();
        mapper = configuration.getObjectMapper();
    }

    String findSnapshot(String methodName) {
        return resolveSnapshots().get(String.format("%s.%s", getClass().getName(), methodName));
    }

    String findSnapshot(String methodName, String scenario) {
        return resolveSnapshots().get(String.format("%s.%s[%s]", getClass().getName(), methodName, scenario));
    }

    Map<String, String> resolveSnapshots() {
        try {
            String fileName = String.format(DOT_SEPARATOR, this.getClass().getSimpleName(), configuration.getFileExtension());
            Path path = Paths.get(configuration.getFilePath(), fileName);
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
    void shouldMatchStringSnapshot() throws JsonProcessingException, InterruptedException {
        String expected = "expected";

        expect(expected).toMatchSnapshot();

        String snapshot = findSnapshot("shouldMatchStringSnapshot");
        List<Object> list = Arrays.asList(mapper.readValue(snapshot, String[].class));

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(expected, list.get(0));
    }

    @Test
    void shouldThrowExceptionOnNull() {
        Assertions.assertThrows(NullPointerException.class,
                () -> expect(null).toMatchSnapshot()
        );
    }

    @Test
    void shouldResolveFirstCallerMethod() throws JsonProcessingException {
        anotherCaller();
    }

    void anotherCaller() throws JsonProcessingException {
        String expected = "expected";
        expect(expected).toMatchSnapshot();

        String snapshot = findSnapshot("shouldResolveFirstCallerMethod");
        List<Object> list = Arrays.asList(mapper.readValue(snapshot, String[].class));

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0), expected);
    }

    @Test
    void shouldCreateSnapshotWithScenario() throws JsonProcessingException {
        String expected = "expected";
        expect(expected).withScenario("scenario").toMatchSnapshot();

        String snapshot = findSnapshot("shouldResolveFirstCallerMethod");
        List<Object> list = Arrays.asList(mapper.readValue(snapshot, String[].class));

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
        expect(expected).withScenario(scenario).toMatchSnapshot();

        String snapshot = findSnapshot("shouldCreateScenariosForParameterizedTests", scenario);
        List<Object> list = Arrays.asList(mapper.readValue(snapshot, String[].class));

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

        expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("list", "date", "sub.sub"))
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

        expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("*"))
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

        expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("*").include("int1", "str1", "list"))
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
