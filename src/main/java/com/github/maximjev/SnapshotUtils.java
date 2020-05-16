package com.github.maximjev;

public class SnapshotUtils {
    static Object concat(Object object, Object... others) {
        Object[] result = new Object[others.length + 1];
        result[0] = object;
        System.arraycopy(others, 0, result, 1, others.length);
        return result;
    }
}
