package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

/**
 * Implements a constraint representing the negation of an other given
 * constraint.
 * 
 * @author Fabio Biselli
 *
 */
public class Neg extends Constraint {

	/**
	 * Build a ConstraintClass that is satisfied if and only if the constraint
	 * <code>c1</code> is not satisfied.
	 * @param c1 constraint
	 */
	public Neg(Constraint c1) {
		super(c1.getProblem());
		Constraint result = (Constraint) c1.negation();
		setImpl(result.getImpl());
	}

	public void post() {
		((Problem) getProblem()).post(this);
	}
	
}
