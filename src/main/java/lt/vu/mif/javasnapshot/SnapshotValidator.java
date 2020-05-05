package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.exception.SnapshotMismatchException;
import lt.vu.mif.javasnapshot.exception.SnapshotFileException;

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
        String method = element.getMethodName();
        String name = snapshot.getScenario().isPresent()
                ? formatScenario(method, snapshot.getScenario().get())
                : method;

        SnapshotFile file = storage.get(element.getClassName());
        String content = serializer.serialize(snapshot);

        if (file.exists(name)) {
            compare(file.get(name), content);
        } else {
            file.push(name, content);
        }
    }

    private String formatScenario(String name, String scenario) {
        return String.format("%s[%s]", name, scenario);
    }

    private StackTraceElement findCaller() {
        StackTraceElement lastCaller = Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> !SKIPPED_CLASSES.contains(s.getClassName()))
                .findFirst()
                .orElseThrow(() -> new SnapshotFileException("Failed to find caller class"));

        return Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> s.getClassName().equals(lastCaller.getClassName()))
                .reduce((first, last) -> last)
                .orElse(lastCaller);
    }

    private void compare(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new SnapshotMismatchException(String.format("expected: %s but was: %s", expected, actual));
        }
    }
}
