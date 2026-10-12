package javax.constraints.impl.constraint;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import org.chocosolver.solver.variables.IntVar;

public class GlobalCardinality extends Constraint {
    public GlobalCardinality(Var[] vars, int[] values, int[] mins, int[] maxs) {
        super(vars[0].getProblem(), "GlobalCardinality");
        if (values.length != mins.length || values.length != maxs.length)
            throw new IllegalArgumentException("values, cardMin and cardMax must have the same length");
        Problem p = (Problem) vars[0].getProblem();
        IntVar[] cards = new IntVar[values.length];
        for (int i = 0; i < values.length; i++) {
            if (mins[i] > maxs[i]) throw new IllegalArgumentException("cardMin must be <= cardMax");
            cards[i] = p.getChocoModel().intVar("cardinality-" + values[i], mins[i], maxs[i]);
        }
        setImpl(p.getChocoModel().globalCardinality(p.createIntVarArray(vars), values, cards, true));
    }

    public GlobalCardinality(Var[] vars, int[] values, Var[] cardinalities) {
        super(vars[0].getProblem(), "GlobalCardinality");
        if (values.length != cardinalities.length)
            throw new IllegalArgumentException("values and cardinalityVars must have the same length");
        Problem p = (Problem) vars[0].getProblem();
        setImpl(p.getChocoModel().globalCardinality(p.createIntVarArray(vars), values,
                p.createIntVarArray(cardinalities), true));
    }

    public GlobalCardinality(Var[] vars, Var[] cardinalities) {
        this(vars, consecutiveValues(cardinalities.length), cardinalities);
    }

    private static int[] consecutiveValues(int length) {
        int[] values = new int[length];
        for (int i = 0; i < length; i++) values[i] = i;
        return values;
    }

    @Override public void post(ConsistencyLevel consistencyLevel) { post(); }
}
