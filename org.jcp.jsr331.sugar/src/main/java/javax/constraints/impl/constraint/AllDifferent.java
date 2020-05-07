package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.ConsistencyLevel;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the ConstraintClass "AllDifferent"
 * 
 * This is one of the most popular ConstraintClass that
 * states that all of the elements within the array of variables "vars" 
 * must take different values from each other.
 * The RI should overload the method "defineNativeImpl" and 
 * may add its own additional constructors.
 */
public class AllDifferent extends Constraint {
    private static final String name = "AllDifferent";
    
    public AllDifferent(Var[] vars) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarAlldifferent(p.toExpr(vars));
    }
    
    private void sugarAlldifferent(Expression xs) {
        Expression c = Expression.create(new Expression[] {
                Expression.ALLDIFFERENT,
                xs
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
        Problem p = (Problem)getProblem();
        try {
            Expression c = _getImpl();
            p.sugarConverter.convert(c);
            post();
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
