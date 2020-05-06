package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.exception.SnapshotMismatchException;
import lt.vu.mif.javasnapshot.exception.SnapshotFileException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

final class SnapshotValidator {
    private static final Set<String> SKIPPED_CLASSES = new HashSet<>();

    private final Map<String, SnapshotFile> currentFiles = new HashMap<>();
    private final SnapshotSerializer serializer;

    SnapshotValidator(SnapshotSerializer serializer) {
        this.serializer = serializer;

        SKIPPED_CLASSES.addAll(Arrays.asList(
                Thread.class.getName(),
                SnapshotValidator.class.getName(),
                Snapshot.class.getName())
        );
    }

    void validate(Snapshot snapshot) {
        StackTraceElement element = findCaller();
        String name = resolveSnapshotName(snapshot, element);

        SnapshotFile file = getSnapshotFile(element.getClassName());
        String content = serializer.serialize(snapshot);

        if (file.exists(name)) {
            compare(file.get(name), content);
        } else {
            file.push(name, content);
        }
    }

    private String resolveSnapshotName(Snapshot snapshot, StackTraceElement element) {
        return snapshot.getScenario().isPresent()
                ? formatScenario(element.getMethodName(), snapshot.getScenario().get())
                : element.getMethodName();
    }

    private SnapshotFile getSnapshotFile(String name) {
        if (currentFiles.containsKey(name)) {
            return currentFiles.get(name);
        }
        SnapshotFile file = new SnapshotFile
                .Builder()
                .withPath(SnapshotConfig.getInstance().getFilePath())
                .withExtension(SnapshotConfig.getInstance().getFileExtension())
                .withStorageType(SnapshotConfig.getInstance().getStorageType())
                .withName(name)
                .build();

        currentFiles.put(name, file);
        return file;
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
