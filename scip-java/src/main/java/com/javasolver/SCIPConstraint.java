package com.javasolver;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** An immutable linear constraint. */
public final class SCIPConstraint {
    final String name;
    final Map<SCIPVariable, Double> terms;
    final double lhs;
    final double rhs;

    SCIPConstraint(String name, Map<SCIPVariable, Double> terms, double lhs, double rhs) {
        this.name = name;
        this.terms = Collections.unmodifiableMap(new LinkedHashMap<>(terms));
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String getName() { return name; }
}
