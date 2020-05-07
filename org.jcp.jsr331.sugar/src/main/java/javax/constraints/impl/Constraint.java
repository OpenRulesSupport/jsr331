package javax.constraints.impl;

import javax.constraints.impl.constraint.And;
import javax.constraints.impl.constraint.IfThen;
import javax.constraints.impl.constraint.Neg;
import javax.constraints.impl.constraint.Or;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the interface "ConstraintClass"
 * 
 * The interface ConstraintClass defines a generic interface for all
 * constraints that could be created and posted within the Problem.
 * Concrete constraints can be defined in two ways:
 * <ul>
 * <li> explicitly in the interface Problem such as "allDifferent"</li>
 * <li> implicitly inside constrained expressions such as sum(vars)
 * or var1.eq(var2). </li>
 * </ul>
 * <br>
 * The interface Problem includes only commonly used and de-facto standardized
 * global constraints in their most popular forms. This API assumes that every API
 * implementation will provide a library of global constraints
 * that implements the interface ConstraintClass and follows the
 * guidelines of the Global ConstraintClass Catalog - see http://www.emn.fr/x-info/sdemasse/gccat/index.html.
 * 
 */
public class Constraint extends AbstractConstraint { 
    public Constraint(javax.constraints.Problem problem) {
        this(problem, "");
    }
    
    public Constraint(javax.constraints.Problem problem, String name) {
        super(problem, name);
    }
    
    public Expression _getImpl() {
        return (Expression)impl;
    }
    
    public void _setImpl(Expression x) {
        impl = x;
    }
    
    /**
     * This method is used in the problem definition to post the
     * constraint. The constraint posting may do two things:
     * 1) initial constraint propagation will be executed;
     * 2) the constraint will be memorized and wait for notification events
     * when the variables it controls are changed.
     * The actual posting logic depends on an underlying CP solver.
     * If the posting was unsuccessful, this method throws a runtime exception.
     * A user may put constraint posting in a try-catch block to react to
     * posting failures.
     * @throws RuntimeException if a failure happened during the posting
     */
    @Override
    public void post() {
        Problem p = (Problem)getProblem();
        try {
            Expression c = _getImpl();
            p.sugarConverter.convert(c);
        } catch (SugarException e) {
            String msg = "Failure to post constraint: " + getName() + " " + e.getMessage();
            p.log(msg);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * This method returns a Var variable that is equal 1 if the constraint
     * is satisfied and equals 0 if it is violated.
     * @return Var with a domain [0;1]: a 1 value indicates the constraint is satisfied,
     *                                  a 0 value indicates the constraint is violated.
     */
    @Override
    public javax.constraints.VarBool asBool() {
        Problem p = (Problem)getProblem();
        try {
            Expression c = _getImpl();
            IntegerVariable x = p.sugarConverter.toIntegerVariable(c.ifThenElse(1, 0));
            VarBool var = new VarBool(p, x.getName());
            var._setImpl(x);
            return var;
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }   
    
    /**
     * Returns an "AND" ConstraintClass. The ConstraintClass "AND" is satisfied if both of the
     *         Constraints "this" and "c" are satisfied. The ConstraintClass "AND" is not satisfied
     *         if at least one of the Constraints "this" or "c" is not satisfied.
     * @param c the ConstraintClass which is part of the new "AND" ConstraintClass
     * @return a ConstraintClass "AND" between the Constraints "this" and "c2".
     */
    @Override
    public javax.constraints.Constraint and(javax.constraints.Constraint c) {
        return new And(this, c);
    }

    /**
     * Returns an "OR" ConstraintClass. The ConstraintClass "OR" is satisfied if either of the
     *         Constraints "this" and "c" is satisfied. The ConstraintClass "OR" is not satisfied
     *         if both of the Constraints "this" and "c" are not satisfied.
     * @param c the ConstraintClass which is part of the new "OR" ConstraintClass
     * @return a ConstraintClass "OR" between the Constraints "this" and "c".
     */
    @Override
    public javax.constraints.Constraint or(javax.constraints.Constraint c) {
        return new Or(this, c);
    }

    /**
     * Returns a ConstraintClass that is satisfied if and only if this constraint is not satisfied.
     * @return a ConstraintClass that is satisfied if and only if this constraint is not satisfied.
     */
    @Override
    public javax.constraints.Constraint negation() {
        return new Neg(this);
    }
    
    /**
     * Returns a ConstraintClass that states the implication: this more or equals to c.
     * In other words, if this constraint is satisfied, then constraint "c"
     * should also be satisfied.
     *
     * @param c the ConstraintClass in the implication.
     * @return a ConstraintClass that means this more or equals to c (if this then c).
     *
     */
    @Override
    public javax.constraints.Constraint implies(javax.constraints.Constraint c) {
        return new IfThen(this,c);
    }
}
