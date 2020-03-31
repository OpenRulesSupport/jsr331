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
 * This is a constant constraint that always will be successfully
 * posted and executed without changing anything in its environment.
 */

public class ConstraintTrue extends AbstractConstraint {

	public ConstraintTrue(Problem problem) {
		super(problem);
	}

	public void post() {
	}

	/**
	 * @return a VarBool variable that is equal to 1
	 */
	public VarBool asBool() {
		Problem p = getProblem();
		VarBool var = p.variableBool();
		p.post(var, "=", 1);
		return var;
	}

}
