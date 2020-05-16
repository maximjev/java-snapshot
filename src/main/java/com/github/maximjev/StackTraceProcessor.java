package com.github.maximjev;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

class StackTraceProcessor {
    private static final Set<String> SKIPPED = new HashSet<>(asList(
            Thread.class.getName(),
            Snapshot.class.getName(),
            StackTraceProcessor.class.getName())
    );

    StackTraceElement findCaller() {
        StackTraceElement firstCaller = Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> !SKIPPED.contains(s.getClassName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Failed to find caller class"));
        return findLastCaller(firstCaller);
    }

    private StackTraceElement findLastCaller(StackTraceElement firstCaller) {
        return Stream.of(Thread.currentThread().getStackTrace())
                .filter(s -> s.getClassName().equals(firstCaller.getClassName()))
                .reduce((first, last) -> last)
                .orElse(firstCaller);
    }
}
