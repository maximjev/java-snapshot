package lt.vu.mif.javasnapshot;

import lt.vu.mif.javasnapshot.exception.SnapshotFileException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.*;


public final class Snapshot {
    private static final Set<String> SKIPPED = new HashSet<>(asList(
            Thread.class.getName(),
            Snapshot.class.getName())
    );

    private final String methodName;
    private final String className;
    private final Object object;

    private String scenario;
    private DynamicFields dynamicFields;

    private Snapshot(Object object, Object... others) {
        this.object = concat(object, others);
        StackTraceElement element = findCaller();
        this.methodName = element.getMethodName();
        this.className = element.getClassName();
    }

    private Object concat(Object object, Object... others) {
        Object[] result = new Object[others.length + 1];
        result[0] = object;
        System.arraycopy(others, 0, result, 1, others.length);
        return result;
    }

    private StackTraceElement findCaller() {
        StackTraceElement lastCaller = Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> !SKIPPED.contains(s.getClassName()))
                .findFirst()
                .orElseThrow(() -> new SnapshotFileException("Failed to find caller class"));

        return Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> s.getClassName().equals(lastCaller.getClassName()))
                .reduce((first, last) -> last)
                .orElse(lastCaller);
    }

    public static Snapshot expect(Object object, Object... others) {
        Objects.requireNonNull(object);
        return new Snapshot(object, others);
    }

    public DynamicFields.Builder withDynamicFields() {
        return new DynamicFields.Builder(this);
    }

    public Snapshot withScenario(String scenario) {
        this.scenario = scenario;
        return this;
    }

    public void toMatchSnapshot() {
        SnapshotConfiguration.getInstance()
                .getSnapshotValidator()
                .validate(this);
    }

    Snapshot withDynamicFields(DynamicFields dynamicFields) {
        this.dynamicFields = dynamicFields;
        return this;
    }

    Object getObject() {
        return object;
    }

    String getMethodName() {
        return methodName;
    }

    String getClassName() {
        return className;
    }

    Optional<String> getScenario() {
        return ofNullable(scenario);
    }

    Optional<DynamicFields> getDynamicFields() {
        return ofNullable(dynamicFields);
    }
}
