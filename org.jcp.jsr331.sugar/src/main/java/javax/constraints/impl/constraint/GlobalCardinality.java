package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.ConsistencyLevel;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the Constraint "GlobalCardinality"
 */
public class GlobalCardinality extends Constraint {
    private static final String name = "GlobalCardinality";
    
    /**
     * For each index i the number of times the value "values[i]" 
     * occurs in the array "vars" should be cardMin[i] and cardMax[i] (inclusive) 
     * @param vars array of constrained integer variables
     * @param values array of integer values within domain of all vars
     * @param cardMin array of integers that serves as lower bounds for values[i]
     * @param cardMax array of integers that serves as upper bounds for values[i]
     * Note that arrays values, cardMin, and cardMax should have the same size 
     * otherwise a RuntimeException will be thrown
     */
    public GlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
        super(vars[0].getProblem(), name);
        if (cardMin.length != values.length || cardMax.length != values.length) {
            throw new RuntimeException("GlobalCardinality error: arrays values, cardMin and cardMax do not have same size");
        }
        Problem p = (Problem)vars[0].getProblem();
        try {
            Expression[] xs2 = new Expression[values.length];
            for (int i = 0; i < values.length; i++) {
                IntegerVariable v = new IntegerVariable(IntegerDomain.create(cardMin[i], cardMax[i]));
                p.sugarCSP.add(v);
                xs2[i] = Expression.create(
                        Expression.create(values[i]), Expression.create(v.getName())
                        );
            }
            sugarGC(p.toExpr(vars), Expression.create(xs2));
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public GlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)vars[0].getProblem();
        Expression[] xs2 = new Expression[values.length];
        for (int i = 0; i < values.length; i++) {
            xs2[i] = Expression.create(Expression.create(values[i]), p.toExpr(cardinalityVars[i]));
        }
        sugarGC(p.toExpr(vars), Expression.create(xs2));
    }

    public GlobalCardinality(Var[] vars, Var[] cardinalityVars) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)vars[0].getProblem();
        Expression[] xs2 = new Expression[vars.length];
        for (int i = 0; i < vars.length; i++) {
            xs2[i] = Expression.create(Expression.create(i), p.toExpr(cardinalityVars[i]));
        }
        sugarGC(p.toExpr(vars), Expression.create(xs2));
    }

    private void sugarGC(Expression xs1, Expression xs2) {
        Expression c = Expression.create(new Expression[] {
                Expression.GLOBAL_CARDINALITY,
                xs1,
                xs2
        });
        _setImpl(c);
    }

    /**
     * This method is used to post the constraint. Additionally to post() 
     * this methods specifies a particular level of consistency that will
     * be selected by an implementation to control the propagation strength of
     * this constraint. If this method is not overloaded by an implementation, it will work as a post(). 
     * @param consistencyLevel ConsistencyLevel
     * @throws RuntimeException if a failure happened during the posting
     */
    @Override
    public void post(ConsistencyLevel consistencyLevel) {
        super.post();
    }

    @Override
    public void post() {
        super.post();
    }
}
