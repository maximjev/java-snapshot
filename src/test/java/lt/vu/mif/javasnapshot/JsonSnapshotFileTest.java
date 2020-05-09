package lt.vu.mif.javasnapshot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.vu.mif.javasnapshot.exception.SnapshotMismatchException;
import lt.vu.mif.javasnapshot.model.TestObject;
import lt.vu.mif.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Arrays.asList;
import static lt.vu.mif.javasnapshot.FieldMatch.match;
import static lt.vu.mif.javasnapshot.Snapshot.expect;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class JsonSnapshotFileTest {

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
            String fileName = String.format("%s.%s", this.getClass().getSimpleName(), configuration.getFileExtension());
            Path path = Paths.get(configuration.getFilePath(), fileName);
            byte[] bytes = Files.readAllBytes(path);
            return mapper.readValue(new String(bytes), LinkedHashMap.class);

        } catch (IOException ignored) {
            return new LinkedHashMap<>();
        }
    }

    @Test
    void shouldUpdateSnapshot() {
        String expected = "expected";
        expect(expected).toUpdate();

        List<Object> list = findSnapshot("shouldUpdateSnapshot");

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(expected, list.get(0));
    }

    @Test
    void shouldMatchStringSnapshot() {
        String expected = "expected";

        expect(expected).toMatchSnapshot();

        List<Object> list = findSnapshot("shouldMatchStringSnapshot");

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
    void shouldThrowMismatchException() {
        Assertions.assertThrows(SnapshotMismatchException.class,
                () -> expect("not expected").toMatchSnapshot()
        );
    }

    @Test
    void shouldResolveFirstCallerMethod() {
        anotherCaller();
    }

    void anotherCaller() {
        String expected = "expected";
        expect(expected).toMatchSnapshot();

        List<Object> list = findSnapshot("shouldResolveFirstCallerMethod");

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0), expected);
    }

    @Test
    void shouldCreateSnapshotWithScenario() {
        String expected = "expected";
        expect(expected).withScenario("scenario").toMatchSnapshot();

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
        expect(expected).withScenario(scenario).toMatchSnapshot();

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

        expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("list", "date", "sub.sub"))
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

        expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("*"))
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

        expect(ref)
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("*").include("int1", "str1", "list"))
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
