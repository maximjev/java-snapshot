package lt.vu.mif.javasnapshot;


import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Streams;
import lt.vu.mif.javasnapshot.match.DynamicFields;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ObjectArrays.*;
import static java.util.Optional.*;


public class Snapshot {
    private final Object object;
    private DynamicFields dynamicFields;

    private Snapshot(Object object) {
        this.object = object;
    }

    public void toMatchSnapshot() {
        SnapshotConfig.getInstance()
                .snapshotValidator()
                .validate(this);
    }

    public Snapshot with(DynamicFields dynamicFields) {
        this.dynamicFields = dynamicFields;
        return this;
    }

    public Object getObject() {
        return object;
    }

    public Optional<DynamicFields> getDynamicFields() {
        return ofNullable(dynamicFields);
    }

    public static Snapshot expect(Object object, Object... others) {
        return new Snapshot(concat(object, others));
    }
}
