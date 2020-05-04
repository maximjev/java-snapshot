package lt.vu.mif.javasnapshot.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;

import java.util.Arrays;

public class SnapshotDiff {
    private static final String NEW_LN = "\n";

    private final String expected;
    private final String actual;

    public SnapshotDiff(String expected, String actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public void print() {
        try {
            Patch<String> patch = DiffUtils.diff(
                    Arrays.asList(expected.trim().split(NEW_LN)),
                    Arrays.asList(actual.trim().split(NEW_LN))
            );

            String error = String.format("Error on: \n %s \n\n %s", actual.trim(), buildDelta(patch));
            throw new SnapshotMismatchException(error);
        } catch (DiffException e) {
            throw new SnapshotMismatchException("Could not compute difference between object and snapshot", e);
        }
    }

    private String buildDelta(Patch<String> patch) {
        return patch
                .getDeltas()
                .stream()
                .map(d -> d.toString() + NEW_LN)
                .reduce(String::concat)
                .get();
    }
}
