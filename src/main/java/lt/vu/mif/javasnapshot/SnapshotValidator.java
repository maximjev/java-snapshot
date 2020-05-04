package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.diff.SnapshotDiff;
import lt.vu.mif.javasnapshot.serialization.SnapshotSerializer;
import lt.vu.mif.javasnapshot.storage.SnapshotFile;
import lt.vu.mif.javasnapshot.storage.SnapshotFileException;
import lt.vu.mif.javasnapshot.storage.SnapshotStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

final class SnapshotValidator {
    private static final Set<String> SKIPPED_CLASSES = new HashSet<>();

    private final SnapshotSerializer serializer;
    private final SnapshotStorage storage;

    SnapshotValidator(SnapshotSerializer serializer, SnapshotStorage storage) {
        this.serializer = serializer;
        this.storage = storage;

        SKIPPED_CLASSES.addAll(List.of(
                Thread.class.getName(),
                SnapshotValidator.class.getName(),
                Snapshot.class.getName())
        );
    }

    void validate(Snapshot snapshot) {
        StackTraceElement element = findCaller();
        String methodName = element.getMethodName();

        SnapshotFile file = storage.get(element.getClassName());
        String content = serializer.serialize(snapshot);

        if (file.exists(methodName)) {
            compare(file.get(methodName), content);
        } else {
            file.push(methodName, content);
        }
    }

    private StackTraceElement findCaller() {
        return Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> !SKIPPED_CLASSES.contains(s.getClassName()))
                .findFirst()
                .orElseThrow(() -> new SnapshotFileException("Failed to find caller class"));
    }

    private void compare(String expected, String actual) {
        if (!expected.equals(actual)) {
            new SnapshotDiff(expected, actual).print();
        }
    }
}
