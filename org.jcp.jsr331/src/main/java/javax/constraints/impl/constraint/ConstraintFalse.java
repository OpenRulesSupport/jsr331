//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.constraint;


import javax.constraints.Problem;
import javax.constraints.VarBool;
import javax.constraints.impl.AbstractConstraint;

/**
 * This is a constant constraint that always will fail when it is
 * posted or executed.
 */

public class ConstraintFalse extends AbstractConstraint {

	public ConstraintFalse(Problem problem) {
		super(problem);
	}

	public void post() {
		throw new RuntimeException("ConstraintFalse");
	}

	/**
	 * @return a VarBool variable that is equal to 0
	 */
	public VarBool asBool() {
		Problem p = getProblem();
		VarBool var = p.variableBool();
		p.post(var, "=", 0);
		return var;
	}	
}
