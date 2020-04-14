package javax.constraints.impl.constraint;

import javax.constraints.Var;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the Constraint "Cardinality"
 */
public class Cardinality extends Constraint {
    private static final String name = "Cardinality";
    
    /**
     * Example: cardinality(vars,cardValue) less than var
     * Here cardinality(vars,cardValue) denotes a constrained integer 
     * variable that is equal to the number of those elements in the 
     * array "vars" that are bound to the "cardValue". 
     * @param vars array of variables
     * @param cardValue int
     * @param oper operator
     * @param var variable
     */
    public Cardinality(Var[] vars, int cardValue, String oper, Var var) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarCount(
                Expression.create(cardValue),
                p.toExpr(vars),
                p.toExprComp(oper),
                p.toExpr(var)
                );
    }
    
    /**
     * Example: cardinality(vars,cardVar) less than var
     * Here cardinality(vars,cardVar) denotes a constrained integer 
     * variable that is equal to the number of those elements in the 
     * array "vars" that are bound to the value of the variable "cardVar"
     * when it is instantiated.
     * @param vars array of variables
     * @param cardVar int variable
     * @param oper operator
     * @param var variable
     */
    public Cardinality(Var[] vars, Var cardVar, String oper, Var var) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarCount(
                p.toExpr(cardVar),
                p.toExpr(vars),
                p.toExprComp(oper),
                p.toExpr(var)
                );
    }
    
    /**
     * Example: cardinality(vars,cardValue) less than value
     * @param vars array of variables
     * @param cardValue int
     * @param oper operator
     * @param value int
     */
    public Cardinality(Var[] vars, int cardValue, String oper, int value) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarCount(
                Expression.create(cardValue),
                p.toExpr(vars),
                p.toExprComp(oper),
                Expression.create(value)
                );
    }
    
    /**
     * Example: cardinality(vars,cardVar) less than value
     * @param vars array of variables
     * @param cardVar Var
     * @param oper operator
     * @param value int
     */
    public Cardinality(Var[] vars, Var cardVar, String oper, int value) {
        super(vars[0].getProblem(), name);
        Problem p = (Problem)getProblem();
        sugarCount(
                p.toExpr(cardVar),
                p.toExpr(vars),
                p.toExprComp(oper),
                Expression.create(value)
                );
    }
    
    /**
     * 
     * @param x1 Expression
     * @param xs1 Expression
     * @param cmp Expression
     * @param x2 Expression
     */
    private void sugarCount(Expression x1, Expression xs1, Expression cmp, Expression x2) {
        Expression c = Expression.create(new Expression[] {
                Expression.COUNT,
                x1,
                xs1,
                cmp,
                x2
        });
        _setImpl(c);
    }
}
