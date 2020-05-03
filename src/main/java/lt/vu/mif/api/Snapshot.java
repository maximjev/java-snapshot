package lt.vu.mif.api;

import lt.vu.mif.config.SnapshotConfig;

public class Snapshot<T> {
    private final T object;
    private DynamicFields dynamicFields;

    public Snapshot(T object) {
        this.object = object;
    }

    public void toMatchSnapshot() {

    }

    public Snapshot with(DynamicFields dynamicFields) {
        this.dynamicFields = dynamicFields;
        return this;
    }

    public T getObject() {
        return object;
    }

    public DynamicFields getDynamicFields() {
        return dynamicFields;
    }
}
