package javax.constraints.impl.constraint;

import javax.constraints.Var;
import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPBoolVar;
import org.maxicp.cp.engine.core.CPSolver;

public class GlobalCardinality extends javax.constraints.impl.Constraint {
    public GlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
        super(vars[0].getProblem(), bounds(vars, values, cardMin, cardMax));
    }

    public GlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {
        super(vars[0].getProblem(), exact(vars, values, cardinalityVars));
    }

    public GlobalCardinality(Var[] vars, Var[] cardinalityVars) {
        this(vars, sequence(cardinalityVars.length), cardinalityVars);
    }

    private static CPBoolVar exact(Var[] vars, int[] values, Var[] cards) {
        check(values.length, cards.length);
        CPBoolVar[] conditions = new CPBoolVar[values.length];
        for (int i = 0; i < values.length; i++)
            conditions[i] = CPFactory.isEq(
                    Cardinality.count(vars, values[i]), Linear.unwrap(cards[i]));
        return and(conditions, solver(vars));
    }

    private static CPBoolVar bounds(
            Var[] vars, int[] values, int[] mins, int[] maxs) {
        check(values.length, mins.length);
        check(values.length, maxs.length);
        CPBoolVar[] conditions = new CPBoolVar[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            conditions[2 * i] = CPFactory.isGe(Cardinality.count(vars, values[i]), mins[i]);
            conditions[2 * i + 1] = CPFactory.isLe(Cardinality.count(vars, values[i]), maxs[i]);
        }
        return and(conditions, solver(vars));
    }

    private static CPBoolVar and(CPBoolVar[] values, CPSolver solver) {
        if (values.length == 0) {
            CPBoolVar result = CPFactory.makeBoolVar(solver);
            result.fix(1);
            return result;
        }
        CPBoolVar[] negated = new CPBoolVar[values.length];
        for (int i = 0; i < values.length; i++) negated[i] = CPFactory.not(values[i]);
        return CPFactory.not(CPFactory.isOr(negated));
    }

    private static CPSolver solver(Var[] vars) {
        return ((javax.constraints.impl.Problem) vars[0].getProblem()).getMaxiCPSolver();
    }

    private static int[] sequence(int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) values[i] = i;
        return values;
    }

    private static void check(int expected, int actual) {
        if (expected != actual)
            throw new IllegalArgumentException("Global-cardinality arrays must have equal length");
    }
}
