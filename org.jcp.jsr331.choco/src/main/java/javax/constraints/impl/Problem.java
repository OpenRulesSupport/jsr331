package javax.constraints.impl;

import java.io.InputStream;
import java.io.OutputStream;

import javax.constraints.Constraint;
import javax.constraints.DomainType;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.impl.constraint.AllDifferent;
import javax.constraints.impl.constraint.Cardinality;
import javax.constraints.impl.constraint.Element;
import javax.constraints.impl.constraint.GlobalCardinality;
import javax.constraints.impl.constraint.Linear;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.slf4j.LoggerFactory;

/** JSR-331 implementation backed by Choco Solver 6. */
public class Problem extends AbstractProblem {
    public static final String JSR331_CHOCO_VERSION =
            "JSR-331 Implementation based on Choco Solver 6.0.1";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("javax.constraints");

    private final Model model;
    private Constraint falseConstraint;
    private Constraint trueConstraint;

    public Problem() { this(""); }

    public Problem(String name) {
        super(name);
        model = new Model(name == null ? "" : name);
        setDomainType(DomainType.DOMAIN_AUTOMATIC);
    }

    @Override public String getImplVersion() { return JSR331_CHOCO_VERSION; }
    @Override public void log(String text) { LOGGER.info(text); }
    @Override public void debug(String text) { LOGGER.debug(text); }
    @Override public void error(String text) { LOGGER.error(text); }

    public Model getChocoModel() { return model; }
    public org.chocosolver.solver.Solver getChocoSolver() { return model.getSolver(); }

    @Override
    public Var createVariable(String name, int min, int max) {
        boolean bounded = getDomainType() == DomainType.DOMAIN_MIN_MAX;
        return new javax.constraints.impl.Var(this, model.intVar(name, min, max, bounded));
    }

    @Override
    public Var variable(String name, int[] domain) {
        javax.constraints.impl.Var var = new javax.constraints.impl.Var(this, model.intVar(name, domain));
        add(var);
        return var;
    }

    @Override
    public VarBool variableBool(String name) {
        javax.constraints.impl.VarBool var = new javax.constraints.impl.VarBool(this, name);
        add(var);
        return var;
    }

    public IntVar[] createIntVarArray(Var[] vars) {
        IntVar[] result = new IntVar[vars.length];
        for (int i = 0; i < vars.length; i++)
            result[i] = ((javax.constraints.impl.Var) vars[i]).getChocoVar();
        return result;
    }

    public void addChocoConstraint(org.chocosolver.solver.constraints.Constraint constraint) {
        constraint.post();
    }

    @Override public void post(Constraint constraint) { constraint.post(); }

    @Override
    public javax.constraints.extra.Reversible addReversible(String name, int value) {
        return new Reversible(this, name, value);
    }

    @Override public Constraint post(Var var, String oper, int value) {
        Constraint c = add(new Linear(var, oper, value)); c.post(); return c;
    }
    @Override public Constraint linear(Var var, String oper, int value) {
        return add(new Linear(var, oper, value));
    }
    @Override public Constraint post(Var left, String oper, Var right) {
        Constraint c = add(new Linear(left, oper, right)); c.post(); return c;
    }
    @Override public Constraint linear(Var left, String oper, Var right) {
        return add(new Linear(left, oper, right));
    }

    @Override public Constraint postElement(int[] array, Var index, String oper, int value) {
        Constraint c = add(new Element(array, index, oper, value)); c.post(); return c;
    }
    @Override public Constraint postElement(int[] array, Var index, String oper, Var value) {
        Constraint c = add(new Element(array, index, oper, value)); c.post(); return c;
    }
    @Override public Constraint postElement(Var[] array, Var index, String oper, int value) {
        Constraint c = add(new Element(array, index, oper, value)); c.post(); return c;
    }
    @Override public Constraint postElement(Var[] array, Var index, String oper, Var value) {
        Constraint c = add(new Element(array, index, oper, value)); c.post(); return c;
    }

    @Override
    public Var sum(Var[] vars) {
        IntVar[] chocoVars = createIntVarArray(vars);
        long min = 0, max = 0;
        for (IntVar var : chocoVars) { min += var.getLB(); max += var.getUB(); }
        IntVar result = model.intVar("sum", clamp(min), clamp(max), true);
        model.sum(chocoVars, "=", result).post();
        return new javax.constraints.impl.Var(this, result);
    }

    @Override
    public Var scalProd(int[] values, Var[] vars) {
        if (values.length != vars.length)
            throw new IllegalArgumentException("values and vars must have the same length");
        IntVar[] chocoVars = createIntVarArray(vars);
        long min = 0, max = 0;
        for (int i = 0; i < values.length; i++) {
            long a = (long) values[i] * chocoVars[i].getLB();
            long b = (long) values[i] * chocoVars[i].getUB();
            min += Math.min(a, b); max += Math.max(a, b);
        }
        IntVar result = model.intVar("scalProd", clamp(min), clamp(max), true);
        model.scalar(chocoVars, values, "=", result).post();
        return new javax.constraints.impl.Var(this, result);
    }

    @Override public Constraint allDiff(Var[] vars) { return add(new AllDifferent(vars)); }

    @Override public Constraint postCardinality(Var[] vars, int cardValue, String oper, int value) {
        Constraint c = add(new Cardinality(vars, cardValue, oper, value)); c.post(); return c;
    }
    @Override public Constraint postCardinality(Var[] vars, int cardValue, String oper, Var value) {
        Constraint c = add(new Cardinality(vars, cardValue, oper, value)); c.post(); return c;
    }
    @Override public Constraint postGlobalCardinality(Var[] vars, int[] values, Var[] cardinalities) {
        Constraint c = add(new GlobalCardinality(vars, values, cardinalities)); c.post(); return c;
    }
    public Constraint postGlobalCardinality(Var[] vars, Var[] cardinalities) {
        Constraint c = add(new GlobalCardinality(vars, cardinalities)); c.post(); return c;
    }
    @Override public Constraint postGlobalCardinality(Var[] vars, int[] values, int[] mins, int[] maxs) {
        Constraint c = add(new GlobalCardinality(vars, values, mins, maxs)); c.post(); return c;
    }

    @Override protected Solver createSolver() { return new javax.constraints.impl.search.Solver(this); }

    public org.chocosolver.solver.constraints.Constraint compare(IntVar var, String oper, int value) {
        return model.arithm(var, normalizeOperator(oper), value);
    }
    public org.chocosolver.solver.constraints.Constraint compare(IntVar left, String oper, IntVar right) {
        return model.arithm(left, normalizeOperator(oper), right);
    }

    public static String normalizeOperator(String oper) {
        if ("==".equals(oper) || "is".equalsIgnoreCase(oper)) return "=";
        if ("is not".equalsIgnoreCase(oper)) return "!=";
        if ("=".equals(oper) || "!=".equals(oper) || "<".equals(oper) || "<=".equals(oper)
                || ">".equals(oper) || ">=".equals(oper)) return oper;
        throw new IllegalArgumentException("Unsupported operator: " + oper);
    }

    @Override public Constraint getFalseConstraint() {
        if (falseConstraint == null)
            falseConstraint = new javax.constraints.impl.Constraint(this, model.falseConstraint());
        return falseConstraint;
    }
    @Override public Constraint getTrueConstraint() {
        if (trueConstraint == null)
            trueConstraint = new javax.constraints.impl.Constraint(this, model.trueConstraint());
        return trueConstraint;
    }

    @Override public void loadFromXML(InputStream in) {
        throw new UnsupportedOperationException("Choco adapter does not support XML import");
    }
    @Override public void storeToXML(OutputStream out, String comment) {
        throw new UnsupportedOperationException("Choco adapter does not support XML export");
    }

    static int clamp(long value) {
        return (int) Math.max(IntVar.MIN_INT_BOUND, Math.min(IntVar.MAX_INT_BOUND, value));
    }
}
