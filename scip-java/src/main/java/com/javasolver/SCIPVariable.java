package com.javasolver;

/** A decision variable owned by a {@link SCIP} model. */
public final class SCIPVariable {
    public enum Type { BINARY, INTEGER, CONTINUOUS }

    final SCIP owner;
    final int index;
    String name;
    double lower = 0.0;
    double upper = Double.POSITIVE_INFINITY;
    double objective;
    Type type = Type.CONTINUOUS;

    SCIPVariable(SCIP owner, int index) {
        this.owner = owner;
        this.index = index;
        this.name = "x_" + index;
    }

    public SCIPVariable name(String value) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("Variable name cannot be empty");
        name = value;
        owner.changed();
        return this;
    }

    public SCIPVariable obj(double value) {
        requireFinite(value, "objective coefficient");
        objective = value;
        owner.changed();
        return this;
    }

    public SCIPVariable bounds(double lb, double ub) {
        if (Double.isNaN(lb) || Double.isNaN(ub) || lb > ub)
            throw new IllegalArgumentException("Invalid variable bounds [" + lb + ", " + ub + "]");
        lower = lb;
        upper = ub;
        owner.changed();
        return this;
    }

    public SCIPVariable lb(double value) { return bounds(value, upper); }
    public SCIPVariable ub(double value) { return bounds(lower, value); }
    public SCIPVariable free() { return bounds(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY); }

    public SCIPVariable binary() {
        type = Type.BINARY;
        lower = 0.0;
        upper = 1.0;
        owner.changed();
        return this;
    }

    public SCIPVariable integer() {
        type = Type.INTEGER;
        owner.changed();
        return this;
    }

    public SCIPVariable continuous() {
        type = Type.CONTINUOUS;
        owner.changed();
        return this;
    }

    public Type getType() { return type; }
    public double getSolution() { return owner.getSolution(this); }
    public String getName() { return name; }

    static void requireFinite(double value, String label) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException(label + " must be finite");
    }

    @Override public String toString() { return name; }
}
