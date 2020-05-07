package javax.constraints.impl.constraint;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the ConstraintClass "Or"
 */
public class Or extends Constraint {
    public Or(javax.constraints.Constraint c1, javax.constraints.Constraint c2) {
        super(c1.getProblem(), "or"); 
        Problem p = (Problem)getProblem();
        sugarOr(p.toExpr(c1), p.toExpr(c2));
    }

    private void sugarOr(Expression c1, Expression c2) {
        Expression c = Expression.create(new Expression[] {
                Expression.OR,
                c1,
                c2
        });
        _setImpl(c);
    }
}
