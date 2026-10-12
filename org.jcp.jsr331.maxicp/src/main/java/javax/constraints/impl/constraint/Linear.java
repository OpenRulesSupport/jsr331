package javax.constraints.impl.constraint;

import javax.constraints.Var;
import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPIntVar;

/** Reified linear relation. */
public class Linear extends javax.constraints.impl.Constraint {
    public Linear(Var left, String oper, Var right) {
        super(left.getProblem(), javax.constraints.impl.Problem.compare(
                unwrap(left), oper, unwrap(right)));
    }

    public Linear(Var var, String oper, int value) {
        super(var.getProblem(), javax.constraints.impl.Problem.compare(
                unwrap(var), oper, value));
    }

    public Linear(int[] coefficients, Var[] vars, String oper, int value) {
        super(vars[0].getProblem(), javax.constraints.impl.Problem.compare(
                scalar(coefficients, vars), oper, value));
    }

    public Linear(Var[] vars, String oper, int value) {
        this(ones(vars.length), vars, oper, value);
    }

    public Linear(int[] coefficients, Var[] vars, String oper, Var var) {
        super(var.getProblem(), javax.constraints.impl.Problem.compare(
                scalar(coefficients, vars), oper, unwrap(var)));
    }

    public Linear(Var[] vars, String oper, Var var) {
        this(ones(vars.length), vars, oper, var);
    }

    private static CPIntVar scalar(int[] coefficients, Var[] vars) {
        if (coefficients.length != vars.length || vars.length == 0)
            throw new IllegalArgumentException("Linear arrays must be non-empty and have equal length");
        CPIntVar[] terms = new CPIntVar[vars.length];
        for (int i = 0; i < vars.length; i++)
            terms[i] = CPFactory.mul(unwrap(vars[i]), coefficients[i]);
        return CPFactory.sum(terms);
    }

    private static int[] ones(int size) {
        int[] result = new int[size];
        java.util.Arrays.fill(result, 1);
        return result;
    }

    static CPIntVar unwrap(Var var) {
        return ((javax.constraints.impl.Var) var).getMaxiCPVar();
    }
}
