package com.github.maximjev;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


class CompatibleSnapshotFormatter {
    public static final String SNAPSHOT_SEPARATOR = "\n\n\n";
    private static final Pattern REGEX = Pattern.compile("(?<name>[^ =]*) *=+ *(?<data>\\[.*\\])[^\\]]*",
            Pattern.MULTILINE + Pattern.DOTALL
    );

    String format(Snapshot snapshot) {
        return snapshot.getScenario().isPresent()
                ? formatScenario(snapshot)
                : formatRegular(snapshot);
    }

    private String formatScenario(Snapshot snapshot) {
        String scenario = snapshot.getScenario().get();
        return String.format("%s.%s[%s]", snapshot.getClassName(), snapshot.getMethodName(), scenario);
    }

    private String formatRegular(Snapshot snapshot) {
        return String.format("%s.%s", snapshot.getClassName(), snapshot.getMethodName());
    }

    String formatRaw(String name, String data) {
        return String.join("=", name, data);
    }

    String join(String entry, String another) {
        return String.join(SNAPSHOT_SEPARATOR, entry, another);
    }

    String[] split(String content){
        return content.split(SNAPSHOT_SEPARATOR);
    }

    Matcher match(String raw) {
        Matcher matcher = REGEX.matcher(raw);
        matcher.matches();
        return matcher;
    }
}
