package javax.constraints.impl.constraint;

import javax.constraints.Var;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the Constraint "Element"
 */
public class Element extends Constraint {
    private static final String name = "Element";
    
    public Element(int[] array, Var indexVar, String oper, int value) {
        super(indexVar.getProblem(), name);
        if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
            throw new RuntimeException("elementAt: invalid index variable");
        Problem p = (Problem)indexVar.getProblem();
        sugarElement(
                p.toExpr(indexVar).add(1),
                p.toExpr(array),
                p.toExprComp(oper),
                Expression.create(value)
                );
    }
    
    public Element(int[] array, Var indexVar, String oper, Var var) {
        super(indexVar.getProblem(), name);
        if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
            throw new RuntimeException("elementAt: invalid index variable");
        Problem p = (Problem)indexVar.getProblem();
        sugarElement(
                p.toExpr(indexVar).add(1),
                p.toExpr(array),
                p.toExprComp(oper),
                p.toExpr(var)
                );
    }
    
    public Element(Var[] vars, Var indexVar, String oper, int value) {
        super(indexVar.getProblem(), name);
        if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
            throw new RuntimeException("elementAt: invalid index variable");
        Problem p = (Problem)indexVar.getProblem();
        sugarElement(
                p.toExpr(indexVar).add(1),
                p.toExpr(vars),
                p.toExprComp(oper),
                Expression.create(value)
                );
    }
    
    public Element(Var[] vars, Var indexVar, String oper, Var var) {
        super(indexVar.getProblem(), name);
        if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
            throw new RuntimeException("elementAt: invalid index variable");
        if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
            throw new RuntimeException("elementAt: invalid index variable");
        Problem p = (Problem)indexVar.getProblem();
        sugarElement(
                p.toExpr(indexVar).add(1),
                p.toExpr(vars),
                p.toExprComp(oper),
                p.toExpr(var)
                );
    }
    
    private void sugarElement(Expression x1, Expression xs1, Expression cmp, Expression x2) {
        Expression c = Expression.create(new Expression[] {
                Expression.ELEMENT,
                x1,
                xs1,
                cmp,
                x2
        });
        _setImpl(c);
    }

}
