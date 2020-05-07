package javax.constraints.impl.constraint;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the ConstraintClass "IfThen"
 */
public class IfThen extends Constraint {
    public IfThen(javax.constraints.Constraint c1, javax.constraints.Constraint c2) {
        super(c1.getProblem(), "implies");
        Problem p = (Problem)getProblem();
        sugarImp(p.toExpr(c1), p.toExpr(c2));
    }

    private void sugarImp(Expression c1, Expression c2) {
        Expression c = Expression.create(new Expression[] {
                Expression.IMP,
                c1,
                c2
        });
        _setImpl(c);
    }
}
