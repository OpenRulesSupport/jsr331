//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.AbstractConstraint;

public class ConstraintGlobalCardinality extends AbstractConstraint {

	Var[] vars;
	int[] values;
	Var[] cardinalityVars;

	public ConstraintGlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {
		super(vars[0].getProblem());
		if (cardinalityVars.length != values.length) {
			throw new RuntimeException("ConstraintGlobalCardinality error: arrays values and cardinalityVars do not have same size");
		}
		
		this.vars = vars;
		this.values = values;
		this.cardinalityVars = cardinalityVars;
	}
	
	
	public void post() {
		for (int i = 0; i < values.length; i++) {
			getProblem().postCardinality(vars,values[i],"=",cardinalityVars[i]);
		}
	}
	
}
