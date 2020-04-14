package javax.constraints.impl.constraint;

import javax.constraints.Var;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the Constraint "Linear"
 */
public class Linear extends Constraint {
    private static final String name = "Linear";

    public Linear(Var var1, String oper, Var var2) {
        super(var1.getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarComp(p.toExprComp(oper), p.toExpr(var1), p.toExpr(var2));
    }
    
    public Linear(Var var, String oper, int value) {
        super(var.getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarComp(p.toExprComp(oper), p.toExpr(var), Expression.create(value));
    }
    
    public Linear(int[] values, Var[] vars, String oper, int value) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarComp(p.toExprComp(oper), p.toSumExpr(values, vars), Expression.create(value));
    }

    public Linear(Var[] vars, String oper, int value) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarComp(p.toExprComp(oper), p.toSumExpr(vars), Expression.create(value));
    }

    public Linear(int[] values, Var[] vars, String oper, Var var) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarComp(p.toExprComp(oper), p.toSumExpr(values, vars), p.toExpr(var));
    }

    public Linear(Var[] vars, String oper, Var var) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarComp(p.toExprComp(oper), p.toSumExpr(vars), p.toExpr(var));
    }
    
    private void sugarComp(Expression cmp, Expression x1, Expression x2) {
        Expression c = Expression.create(new Expression[] {
                cmp,
                x1,
                x2
        });
        _setImpl(c);
    }
}
