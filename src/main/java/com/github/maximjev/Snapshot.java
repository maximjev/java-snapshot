package com.github.maximjev;


import java.util.Objects;
import java.util.Optional;

import static com.github.maximjev.SnapshotUtils.concat;
import static java.util.Optional.ofNullable;


public final class Snapshot {

    private final SnapshotValidator validator;

    private final String methodName;
    private final String className;
    private final Object object;

    private String scenario;
    private DynamicFields dynamicFields;

    private Snapshot(Object object, Object... others) {
        StackTraceElement element = new StackTraceProcessor().findCaller();
        this.validator = SnapshotConfiguration.INSTANCE
                .getSnapshotValidator();
        this.object = concat(object, others);
        this.methodName = element.getMethodName();
        this.className = element.getClassName();
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
        validator.validate(this);
    }

    public void toMatchInlineSnapshot(String inline) {
        Objects.requireNonNull(inline);
        validator.validateInline(this, inline);
    }

    public void toUpdate() {
        validator.update(this);
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
