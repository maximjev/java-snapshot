package lt.vu.mif.javasnapshot;


import java.util.Objects;
import java.util.Optional;

import static com.google.common.collect.ObjectArrays.*;
import static java.util.Optional.*;


public class Snapshot {
    private final Object object;
    private DynamicFields dynamicFields;
    private String scenario;

    private Snapshot(Object object) {
        Objects.requireNonNull(object);
        this.object = object;
    }

    public void toMatchSnapshot() {
        SnapshotConfig.getInstance()
                .snapshotValidator()
                .validate(this);
    }

    public DynamicFields.Builder withDynamicFields() {
        return new DynamicFields.Builder(this);
    }

    public Snapshot withScenario(String scenario) {
        this.scenario = scenario;
        return this;
    }

    Snapshot withDynamicFields(DynamicFields dynamicFields) {
        this.dynamicFields = dynamicFields;
        return this;
    }

    Object getObject() {
        return object;
    }

    Optional<DynamicFields> getDynamicFields() {
        return ofNullable(dynamicFields);
    }

    Optional<String> getScenario() {
        return ofNullable(scenario);
    }

    public static Snapshot expect(Object object, Object... others) {
        Objects.requireNonNull(object);
        return new Snapshot(concat(object, others));
    }
}
