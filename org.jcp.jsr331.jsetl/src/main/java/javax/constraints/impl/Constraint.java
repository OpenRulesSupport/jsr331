package javax.constraints.impl;


/**
 * This class implements the JSR331 constraint class extending the common
 * implementation AbstractConstraint. The implementation is based on the solver
 * JSetL.
 * 
 * @author Fabio Biselli
 *
 */
public class Constraint extends AbstractConstraint {
	
	/**
	 * Build a new Constraint.
	 * 
	 * @param problem the problem which the constraint is related.
	 */
	public Constraint(javax.constraints.Problem problem) {
		super(problem);
		setImpl(new JSetL.Constraint());
	}

	/**
	 * Build a new Constraint from a instance of the class Constraint of
	 * JSetL solver.
	 * 
	 * @param problem the problem which the constraint is related
	 * @param constraint the JSetL constraint.
	 */
	public Constraint(javax.constraints.Problem problem, JSetL.Constraint constraint) {
		super(problem);
		setImpl(constraint);
	}

	/**
	 * Return a boolean variable that is equals 1 if the variable <code>this</code> is 
	 * satisfied, 0 otherwise.
	 */
	public javax.constraints.VarBool asBool() {
		if (getImpl() == null) {
			throw new RuntimeException("Constraint " + getName() + 
					" has no implementation for the method toVar()." +
					" It cannot be used in logical expressions.");
		}
		Problem p = (Problem) getProblem();
		JSetL.Constraint constraint = (JSetL.Constraint) getImpl();
		if (constraint == null) {
			String msg = "Failure to convert constraint " + getName() + 
			        " to VarBool. Not implemented.";
			getProblem().log(msg);
			throw new RuntimeException(msg);
		}
		VarBool var = new VarBool(p, getName()+".asBool");
		p.post(this.implies(p.linear(var, "=", 1)));
		p.post(p.linear(var, "=", 1).implies(this));
		return var;
	}
	
	/**
	 * Getter method for the JSetL.Constraint.
	 * 
	 * @return the JSetL.Constraint.
	 */
	public JSetL.Constraint getConstraint() {
		return (JSetL.Constraint) getImpl();
	}
	
	/**
	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if both 
	 * of the Constraints "this" and "c" are satisfied. The Constraint "AND" 
	 * is not satisfied if at least one of the Constraints "this" or "c" is 
	 * not satisfied.
	 * 
	 * @param c the Constraint which is part of the new "AND" Constraint.
	 * 
	 * @return a Constraint "AND" between the Constraints "this" and "c2".
	 */
	public Constraint and(javax.constraints.Constraint c) {
		JSetL.Constraint constraint = ((Constraint) c).getConstraint();
		Constraint result = new Constraint(getProblem(), 
				this.getConstraint().and(constraint));
		return result;
	}
	
	/**
	 * Returns an "OR" Constraint. The Constraint "OR" is satisfied if either 
	 * of the Constraints "this" and "c" is satisfied. The Constraint "OR" is 
	 * not satisfied if both of the Constraints "this" and "c" are not 
	 * satisfied.
	 * 
	 * @param c the Constraint which is part of the new "OR" Constraint.
	 * 
	 * @return a Constraint "OR" between the Constraints "this" and "c".
	 */
	public Constraint or(javax.constraints.Constraint c) {
		JSetL.Constraint constraint = ((Constraint) c).getConstraint();
		Constraint result = new Constraint(getProblem(), 
				this.getConstraint().orTest(constraint));
		return result;
	}
	
	/**
	 * Returns a Constraint that is satisfied if and only if this constraint 
	 * is not satisfied.
	 * 
	 * @return a Constraint that is satisfied if and only if this 
	 * constraint is not satisfied.
	 */
	public Constraint negation() {
		Constraint result = new Constraint(getProblem(), 
				this.getConstraint().notTest());
		return result;		
	}
	
	/**
	 * Returns a Constraint that states the implication: this more or equals c.
	 * In other words, if this constraint is satisfied, then constraint "c"
	 * should also be satisfied.
	 * 
	 * @param c the Constraint in the implication.
	 * 
	 * @return a Constraint that means this more or equals c (if this then c).
	 *
	 */
	public Constraint implies(javax.constraints.Constraint c) {	
		JSetL.Constraint constraint = ((Constraint) c).getConstraint();
		Constraint result = new Constraint(getProblem(), 
				this.getConstraint().impliesTest(constraint));
		return result;
	}
}

