package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

/**
 * Implements a constraint that represent the implication
 * between two given constraints.
 * 
 * @author Fabio Biselli
 *
 */
public class IfThen extends Constraint {

	/**
	 * Returns a Constraint that states the implication: c1 more or equals to c2.
	 * In other words, if c1 is satisfied, then constraint c2
	 * should also be satisfied.
	 * 
	 * @param c1 the antecedent Constraint in the implication
	 * @param c2 the consequent Constraint in the implication.
	 *
	 */
	public IfThen(Constraint c1, Constraint c2) {
		super(c1.getProblem());
		Constraint result = (Constraint) c1.implies(c2);
		setImpl(result.getImpl());
	}
	
	public void post() {
		((Problem) getProblem()).post(this);
	}

}
