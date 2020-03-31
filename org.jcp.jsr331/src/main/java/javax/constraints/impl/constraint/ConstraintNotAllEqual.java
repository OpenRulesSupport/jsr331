//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.constraint;

import javax.constraints.Constraint;

import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.impl.AbstractConstraint;

/**
 * This constraint states that not all elements within an array of vars should be the same 
 *
 */
public class ConstraintNotAllEqual extends AbstractConstraint {

	Constraint constraint;

	public ConstraintNotAllEqual(Var[] vars) {
		super(vars[0].getProblem());
		int n = vars.length-1;
		VarBool[] equalities = new VarBool[n];
		Problem p = getProblem();
		for (int i = 0; i < n; i++) {
			equalities[i] = p.linear(vars[i],"=",vars[i+1]).asBool();
		}
		constraint = p.post(equalities, "<", n);
	}
	
	public ConstraintNotAllEqual(Var[] vars, int[] values) {
		super(vars[0].getProblem());
		
		if (values.length != vars.length)
			throw new RuntimeException("ConstraintNotAllEqual(Var[] vars, int[] values) requires arrays of the same length");
		int n = vars.length;
		VarBool[] equalities = new VarBool[n];
		Problem p = getProblem();
		for (int i = 0; i < n; i++) {
			equalities[i] = p.linear(vars[i],"=",values[i]).asBool();
		}
		constraint = getProblem().post(equalities, "<", n);
	}

	public void post() {
		constraint.post();
	}

}
