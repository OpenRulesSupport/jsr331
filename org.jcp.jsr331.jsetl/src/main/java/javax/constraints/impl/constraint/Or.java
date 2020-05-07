package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

/**
 * Implement a constraint that represents the disjunction between two given
 * constraints.
 * 
 * @author Fabio Biselli
 *
 */
public class Or extends Constraint {

	/**
	 * Build a ConstraintClass that is satisfied if either
	 * of the Constraints "c1" and "c2" is satisfied. The ConstraintClass is
	 * not satisfied if both of the Constraints "c1" and "c2" are not 
	 * satisfied.
	 * 
	 * @param c1 the first ConstraintClass which is part of the new "OR" ConstraintClass
	 * @param c2 the first ConstraintClass which is part of the new "OR" ConstraintClass
	 */
	public Or(Constraint c1, Constraint c2) {
		super(c1.getProblem());
		Constraint result = (Constraint) c1.or(c2);
		setImpl(result.getImpl());
	}

	public void post() {
		((Problem) getProblem()).post(this);
	}
	
}
