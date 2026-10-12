package com.javasolver;

import java.util.LinkedHashMap;
import java.util.Map;

/** Fluent linear-expression builder. */
public final class SCIPExpression {
    private final SCIP owner;
    private final Map<SCIPVariable, Double> terms = new LinkedHashMap<>();
    private double constant;

    SCIPExpression(SCIP owner) { this.owner = owner; }

    public SCIPExpression add(SCIPVariable variable) { return add(1.0, variable); }

    public SCIPExpression add(double coefficient, SCIPVariable variable) {
        if (variable == null || variable.owner != owner)
            throw new IllegalArgumentException("Variable belongs to another model");
        SCIPVariable.requireFinite(coefficient, "coefficient");
        double value = terms.containsKey(variable) ? terms.get(variable) + coefficient : coefficient;
        if (value == 0.0) terms.remove(variable); else terms.put(variable, value);
        return this;
    }

    public SCIPExpression add(double value) {
        SCIPVariable.requireFinite(value, "constant");
        constant += value;
        return this;
    }

    public SCIPConstraint leq(double rhs) { return owner.addConstraint(terms, Double.NEGATIVE_INFINITY, rhs - constant); }
    public SCIPConstraint geq(double lhs) { return owner.addConstraint(terms, lhs - constant, Double.POSITIVE_INFINITY); }
    public SCIPConstraint eq(double value) { return owner.addConstraint(terms, value - constant, value - constant); }
    public SCIPConstraint range(double lhs, double rhs) { return owner.addConstraint(terms, lhs - constant, rhs - constant); }

    public void asObjective() {
        owner.setObjective(terms, constant);
    }
}
