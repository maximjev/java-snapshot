package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.model.TestObject;
import lt.vu.mif.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import static java.util.Arrays.asList;
import static lt.vu.mif.javasnapshot.Snapshot.expect;
import static lt.vu.mif.javasnapshot.DynamicFields.dynamicFields;
import static lt.vu.mif.javasnapshot.FieldMatch.match;

public class SnapshotTest {

    private static String FILE_PATH = "src/test/java/__snapshots__";

    @BeforeAll
    static void setup() {
        SnapshotConfig.getInstance()
                .withFilePath(FILE_PATH);
    }

    @AfterAll
    static void teardown() throws IOException {
        Files.delete(Paths.get(FILE_PATH));
    }

    @Test
    void shouldMatchSnapshot() {
        expect("lol").toMatchSnapshot();
    }

    @Test
    void shouldM() {
        expect("sss").toMatchSnapshot();
    }

    @Test
    void shouldObject() {
        TestObject ref = new TestObject();
        ref.setInt1(1);
        ref.setDate(new Date());
        ref.setStr1("asdssf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        expect(ref).scenario("123").with(dynamicFields(TestObject.class, match().exclude("list", "date", "sub.sub")))
                .toMatchSnapshot();

    }
}
