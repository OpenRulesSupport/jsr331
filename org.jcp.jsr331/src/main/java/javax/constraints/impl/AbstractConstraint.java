//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.Probability;
import javax.constraints.impl.constraint.ConstraintFalse;

/**
 * This is a base implementation for all
 */

abstract public class AbstractConstraint extends CommonBase implements javax.constraints.Constraint {
	
	public AbstractConstraint(Problem problem) {
		this(problem,"");
	}
	
	public AbstractConstraint(Problem problem, String name) {
		super(problem,name);
		setImpl(null);
	}
	
	/**
	 * This method takes a constraint's implementation and uses its own 
	 * RI-specific post-method. 
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post() {
		AbstractProblem p = (AbstractProblem) getProblem();
		p.post(this);
//		throw new RuntimeException("The Constraint's method post should be overloaded by this implementation");
	}
	
	/**
	 * This method is used to post the constraint. Additionally to post() 
	 * this methods specifies a particular level of consistency that will
	 * be selected by an implementation to control the propagation strength of
	 * this constraint. If this method is not overloaded by an implementation, it will work as a post(). 
	 * @param consistencyLevel ConsistencyLevel
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(ConsistencyLevel consistencyLevel) {
		post();
//		throw new RuntimeException("The Constraint's method post should be overloaded by this RI");
	}
	
	/**
	 * This method is used to post the constraint assuming that it can be violated. 
	 * The parameter probability defines a probability that this constraint 
	 * will not be violated. If it's ALWAYS this is the same as a regular constraint posting.
	 * If it's NEVER, the negation of this constraint will be posted. 
     * If it's ALWAYS, this constraint will be posted as a regular hard constraint. 
	 * All other values allow the constraint to be violated with certain penalties. 
	 * For instance, probability LOW invokes higher penalty to compare with probability HIGH, but 
	 * a lower penalty to compare with VERY_LOW. 
	 * The Problem' method getTotalConstraintViolation() returns a variable that could be minimized
	 * to find a solution that may all synchronize all relative constraint violations. 
	 * Thus, posting related constraints with different probabilities
	 * may resolve their conflicts.
	 * @param name of the constraint
	 * @param probability Probability
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(String name, Probability probability) {
		AbstractProblem p = (AbstractProblem)getProblem();
		if (probability.getValue() == Probability.ALWAYS.getValue()) {
			this.post();
			return;
		}
		if (probability.getValue() == Probability.NEVER.getValue()) {
		    this.negation().post();
			return;
		}
		p.addConstraintWithProbability(name, this, probability);		
	}
	
	/**
	 * This method returns a Var variable that is equal 1 if the constraint
	 * is satisfied and equals 0 if it is violated.
	 * Each constraint that participates in logical expressions should implement this method.
	 * 
	 * @return Var with a domain [0;1]: a 1 value indicates the constraint is satisfied,
	 *                                  a 0 value indicates the constraint is violated.
	 */
	public VarBool asBool() {
		throw new RuntimeException("Constraint " + getName() + 
				" has no implementation for the method asBool()." +
				" It cannot be used in logical expressions.");
	}
	
	/**
	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if both of the
	 * 		   Constraints "this" and "c" are satisfied. The Constraint "AND" is not satisfied
	 * 		   if at least one of the Constraints "this" or "c" is not satisfied.
	 * @param c the Constraint which is part of the new "AND" Constraint
	 * @return a Constraint "AND" between the Constraints "this" and "c2".
	 */
	public Constraint and(Constraint c) {
		return this.and(c);
	}

	/**
	 * Returns an "OR" Constraint. The Constraint "OR" is satisfied if either of the
	 * 		   Constraints "this" and "c" is satisfied. The Constraint "OR" is not satisfied
	 * 		   if both of the Constraints "this" and "c" are not satisfied.
	 * @param c the Constraint which is part of the new "OR" Constraint
	 * @return a Constraint "OR" between the Constraints "this" and "c".
	 */
	public Constraint or(Constraint c) {
		return this.or(c);
	}

	/**
	 * Returns a Constraint that is satisfied if and only if this constraint is not satisfied.
	 * @return a Constraint that is satisfied if and only if this constraint is not satisfied.
	 */
	public Constraint negation() {
		return this.implies(getProblem().getFalseConstraint());
	}
	
	/**
	 * Returns a Constraint that states the implication: if this then c.
	 * In other words, if this constraint is satisfied, then constraint "c"
	 * should also be satisfied.
	 *
	 * @param c the Constraint in the implication.
	 * @return a Constraint that means "if this then c".
	 *
	 */
	public Constraint implies(Constraint c) {
		throw new RuntimeException("The Constraint method implies should be overloaded by the RI");
	}
	
}
