package lt.vu.mif.javasnapshot.snapshot;

import lt.vu.mif.javasnapshot.SnapshotConfiguration;
import lt.vu.mif.javasnapshot.model.TestObject;
import lt.vu.mif.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static java.util.Arrays.asList;
import static lt.vu.mif.javasnapshot.Snapshot.expect;
import static lt.vu.mif.javasnapshot.FieldMatch.match;

public class SnapshotTest {
    private static final String FILE_PATH = "src/test/java/__snapshots__";

    @BeforeAll
    static void setup() {
        new SnapshotConfiguration.Builder()
                .withFilePath(FILE_PATH)
                .build();
    }

//    @AfterAll
    static void teardown() {
        try {
            Files.walk(Paths.get(FILE_PATH))
                    .map(Path::toFile)
                    .filter(File::exists)
                    .forEach(File::deleteOnExit);
        } catch (IOException ignored) {
        }
    }

    @Test
    void shouldMatchStringSnapshot() {
        expect("String").toMatchSnapshot();
    }

    @Test
    void shouldThrowExceptionOnNull() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            expect(null).toMatchSnapshot();
        });
    }

    @Test
    void shouldResolveFirstCallerMethod() {
        anotherCaller();
    }

    void anotherCaller() {
        expect("something").toMatchSnapshot();
    }

    @Test
    void shouldCreateSnapshotWithScenario() {
        expect("something").withScenario("scenario").toMatchSnapshot();
    }

    @ParameterizedTest(name = "{0} test")
    @ValueSource(strings = {
            "first",
            "second",
            "third",
            "fourth"
    })
    void shouldCreateScenariosForParameterizedTests(String value) {
        expect("something").withScenario(value).toMatchSnapshot();
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

        expect(ref).withScenario("123")
                .withDynamicFields()
                .onClass(TestObject.class, match().exclude("list", "date", "sub.sub"))
                .build()
                .toMatchSnapshot();
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
    }
}
