package javax.constraints.impl.constraint;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the Constraint "Neg"
 */
public class Neg extends Constraint {

    public Neg(javax.constraints.Constraint c1) {
        super(c1.getProblem(), "neg"); 
        Problem p = (Problem)getProblem();
        sugarNot(p.toExpr(c1));
    }
    
    private void sugarNot(Expression c1) {
        Expression c = Expression.create(new Expression[] {
                Expression.NOT,
                c1
        });
        _setImpl(c);
    }
}
