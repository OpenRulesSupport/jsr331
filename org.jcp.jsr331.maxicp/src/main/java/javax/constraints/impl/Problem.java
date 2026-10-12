package javax.constraints.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.constraints.DomainType;
import javax.constraints.Oper;
import javax.constraints.VarBool;

import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPBoolVar;
import org.maxicp.cp.engine.core.CPConstraint;
import org.maxicp.cp.engine.core.CPIntVar;
import org.maxicp.cp.engine.core.CPSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JSR-331 problem implementation backed by MaxiCP. */
public class Problem extends AbstractProblem {
    public static final String JSR331_MAXICP_VERSION =
            "JSR-331 implementation based on MaxiCP 0.0.2";
    private static final Logger LOGGER = LoggerFactory.getLogger("javax.constraints");
    private final CPSolver maxiCPSolver;

    public Problem() { this(""); }

    public Problem(String name) {
        super(name);
        maxiCPSolver = CPFactory.makeSolver();
        setDomainType(DomainType.DOMAIN_AUTOMATIC);
        if (name != null && !name.isEmpty()) log("Problem: " + name);
    }

    @Override public String getImplVersion() { return JSR331_MAXICP_VERSION; }
    public CPSolver getMaxiCPSolver() { return maxiCPSolver; }

    @Override
    public javax.constraints.Var createVariable(String name, int min, int max) {
        return new javax.constraints.impl.Var(this, name, min, max);
    }

    @Override
    public javax.constraints.Var variable(String name, int[] domain) {
        if (domain == null || domain.length == 0)
            throw new IllegalArgumentException("A variable domain must not be empty");
        Set<Integer> values = new LinkedHashSet<>();
        Arrays.stream(domain).forEach(values::add);
        javax.constraints.impl.Var var =
                new javax.constraints.impl.Var(this, name, CPFactory.makeIntVar(maxiCPSolver, values));
        add(var);
        return var;
    }

    @Override
    public VarBool variableBool(String name) {
        javax.constraints.impl.VarBool var =
                new javax.constraints.impl.VarBool(this, name, CPFactory.makeBoolVar(maxiCPSolver));
        add(var);
        return var;
    }

    public CPIntVar[] toMaxiCP(javax.constraints.Var[] vars) {
        CPIntVar[] result = new CPIntVar[vars.length];
        for (int i = 0; i < vars.length; i++)
            result[i] = ((javax.constraints.impl.Var) vars[i]).getMaxiCPVar();
        return result;
    }

    @Override public void post(javax.constraints.Constraint constraint) { constraint.post(); }

    @Override
    public javax.constraints.Constraint linear(javax.constraints.Var var, String oper, int value) {
        return add(new javax.constraints.impl.constraint.Linear(var, oper, value));
    }

    @Override
    public javax.constraints.Constraint linear(
            javax.constraints.Var left, String oper, javax.constraints.Var right) {
        return add(new javax.constraints.impl.constraint.Linear(left, oper, right));
    }

    @Override
    public javax.constraints.Constraint post(javax.constraints.Var var, String oper, int value) {
        javax.constraints.Constraint c = linear(var, oper, value);
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint post(
            javax.constraints.Var left, String oper, javax.constraints.Var right) {
        javax.constraints.Constraint c = linear(left, oper, right);
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint allDiff(javax.constraints.Var[] vars) {
        return add(new javax.constraints.impl.constraint.AllDifferent(vars));
    }

    @Override
    public javax.constraints.Constraint postCardinality(
            javax.constraints.Var[] vars, int cardValue, String oper, int value) {
        javax.constraints.Constraint c = add(
                new javax.constraints.impl.constraint.Cardinality(vars, cardValue, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postCardinality(
            javax.constraints.Var[] vars, int cardValue,
            String oper, javax.constraints.Var value) {
        javax.constraints.Constraint c = add(
                new javax.constraints.impl.constraint.Cardinality(vars, cardValue, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postCardinality(
            javax.constraints.Var[] vars, javax.constraints.Var cardValue,
            String oper, int value) {
        javax.constraints.Constraint c = add(
                new javax.constraints.impl.constraint.Cardinality(vars, cardValue, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postCardinality(
            javax.constraints.Var[] vars, javax.constraints.Var cardValue,
            String oper, javax.constraints.Var value) {
        javax.constraints.Constraint c = add(
                new javax.constraints.impl.constraint.Cardinality(vars, cardValue, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postGlobalCardinality(
            javax.constraints.Var[] vars, int[] values,
            javax.constraints.Var[] cardinalityVars) {
        javax.constraints.Constraint c = add(
                new javax.constraints.impl.constraint.GlobalCardinality(
                        vars, values, cardinalityVars));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postGlobalCardinality(
            javax.constraints.Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
        javax.constraints.Constraint c = add(
                new javax.constraints.impl.constraint.GlobalCardinality(
                        vars, values, cardMin, cardMax));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint getTrueConstraint() {
        CPBoolVar truth = CPFactory.makeBoolVar(maxiCPSolver);
        truth.fix(1);
        return new javax.constraints.impl.Constraint(this, truth);
    }

    @Override
    public javax.constraints.Constraint getFalseConstraint() {
        CPBoolVar truth = CPFactory.makeBoolVar(maxiCPSolver);
        truth.fix(0);
        return new javax.constraints.impl.Constraint(this, truth);
    }

    @Override
    public javax.constraints.Constraint postElement(
            int[] values, javax.constraints.Var index, String oper, int value) {
        javax.constraints.Constraint c =
                add(new javax.constraints.impl.constraint.Element(values, index, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postElement(
            int[] values, javax.constraints.Var index, String oper, javax.constraints.Var value) {
        javax.constraints.Constraint c =
                add(new javax.constraints.impl.constraint.Element(values, index, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postElement(
            javax.constraints.Var[] values, javax.constraints.Var index, String oper, int value) {
        javax.constraints.Constraint c =
                add(new javax.constraints.impl.constraint.Element(values, index, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postElement(
            javax.constraints.Var[] values, javax.constraints.Var index,
            String oper, javax.constraints.Var value) {
        javax.constraints.Constraint c =
                add(new javax.constraints.impl.constraint.Element(values, index, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Var element(int[] values, javax.constraints.Var index) {
        CPIntVar result = CPFactory.element(values,
                ((javax.constraints.impl.Var) index).getMaxiCPVar());
        return new javax.constraints.impl.Var(this, "element", result);
    }

    @Override
    public javax.constraints.Var element(
            javax.constraints.Var[] values, javax.constraints.Var index) {
        CPIntVar result = CPFactory.element(toMaxiCP(values),
                ((javax.constraints.impl.Var) index).getMaxiCPVar());
        return new javax.constraints.impl.Var(this, "element", result);
    }

    @Override
    public javax.constraints.Var sum(javax.constraints.Var[] vars) {
        if (vars.length == 0) return variable(0, 0);
        return new javax.constraints.impl.Var(this, "sum", CPFactory.sum(toMaxiCP(vars)));
    }

    @Override
    public javax.constraints.Var scalProd(int[] coefficients, javax.constraints.Var[] vars) {
        if (coefficients.length != vars.length)
            throw new IllegalArgumentException("Coefficient and variable arrays must have equal length");
        if (vars.length == 0) return variable(0, 0);
        CPIntVar[] terms = new CPIntVar[vars.length];
        for (int i = 0; i < vars.length; i++)
            terms[i] = CPFactory.mul(
                    ((javax.constraints.impl.Var) vars[i]).getMaxiCPVar(), coefficients[i]);
        return new javax.constraints.impl.Var(this, "scalProd", CPFactory.sum(terms));
    }

    public javax.constraints.Constraint constraint(CPConstraint constraint) {
        return new javax.constraints.impl.Constraint(this, constraint);
    }

    public javax.constraints.Constraint constraint(CPBoolVar truth) {
        return new javax.constraints.impl.Constraint(this, truth);
    }

    public static CPBoolVar compare(CPIntVar left, String operator, int right) {
        return switch (normalize(operator)) {
            case EQ -> CPFactory.isEq(left, right);
            case NEQ -> CPFactory.isNeq(left, right);
            case LT -> CPFactory.isLt(left, right);
            case LE -> CPFactory.isLe(left, right);
            case GT -> CPFactory.isGt(left, right);
            case GE -> CPFactory.isGe(left, right);
        };
    }

    public static CPBoolVar compare(CPIntVar left, String operator, CPIntVar right) {
        return switch (normalize(operator)) {
            case EQ -> CPFactory.isEq(left, right);
            case NEQ -> CPFactory.isNeq(left, right);
            case LT -> CPFactory.isLt(left, right);
            case LE -> CPFactory.isLe(left, right);
            case GT -> CPFactory.isGt(left, right);
            case GE -> CPFactory.isGe(left, right);
        };
    }

    private static Oper normalize(String operator) {
        return switch (operator.trim().toLowerCase()) {
            case "=", "==", "is" -> Oper.EQ;
            case "!=", "is not" -> Oper.NEQ;
            case "<" -> Oper.LT;
            case "<=" -> Oper.LE;
            case ">" -> Oper.GT;
            case ">=" -> Oper.GE;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    @Override
    protected javax.constraints.Solver createSolver() {
        return new javax.constraints.impl.search.Solver(this);
    }

    @Override public void log(String text) { LOGGER.info(text); }
    @Override public void debug(String text) { LOGGER.debug(text); }
    @Override public void error(String text) { LOGGER.error(text); }

    @Override
    public void loadFromXML(InputStream in) {
        throw new UnsupportedOperationException(
                "JSR-331 XML import is not supported by the MaxiCP adapter");
    }

    @Override
    public void storeToXML(OutputStream out, String comment) {
        throw new UnsupportedOperationException(
                "JSR-331 XML export is not supported by the MaxiCP adapter");
    }
}
