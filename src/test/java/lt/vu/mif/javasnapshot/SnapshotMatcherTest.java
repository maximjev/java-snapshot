package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.model.TestObject;
import lt.vu.mif.javasnapshot.model.TestSubobject;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static lt.vu.mif.javasnapshot.Snapshot.expect;
import static lt.vu.mif.javasnapshot.match.DynamicFields.dynamicFields;
import static lt.vu.mif.javasnapshot.match.FieldMatch.match;

public class SnapshotMatcherTest {

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
        ref.setStr1("asdf");
        ref.setList(asList("red", "blue", "green"));
        ref.setStringArray(new String[]{"apple", "banana"});
        ref.setSub(new TestSubobject("subvalue", new TestSubobject("subsubvalue")));

        expect(ref).with(dynamicFields(TestObject.class, match().exclude("str1", "list", "stringArray", "sub")))
                .toMatchSnapshot();
    }
}
