package javax.constraints.impl.constraint;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the Constraint "And"
 */
public class And extends Constraint {
    public And(javax.constraints.Constraint c1, javax.constraints.Constraint c2) {
        super(c1.getProblem(), "and"); 
        Problem p = (Problem)getProblem();
        sugarAnd(p.toExpr(c1), p.toExpr(c2));
    }
    
    private void sugarAnd(Expression c1, Expression c2) {
        Expression c = Expression.create(new Expression[] {
                Expression.AND,
                c1,
                c2
        });
        _setImpl(c);
    }
}
