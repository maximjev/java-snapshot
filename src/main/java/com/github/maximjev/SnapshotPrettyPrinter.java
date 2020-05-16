package com.github.maximjev;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;

class SnapshotPrettyPrinter extends DefaultPrettyPrinter {
    @Override
    public DefaultPrettyPrinter withSeparators(Separators separators) {
        this._separators = separators;
        this._objectFieldValueSeparatorWithSpaces = new StringBuilder()
                .append(separators.getObjectFieldValueSeparator())
                .append(" ")
                .toString();
        return this;
    }
}
