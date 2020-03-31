//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search.goal;

import javax.constraints.Constraint;

public class GoalConstraint extends Goal {
	Constraint constraint;
	public GoalConstraint(Constraint constraint) {
		super(constraint.getProblem().getSolver(),constraint.getName());
		this.constraint = constraint;
	}

	@Override
	public Goal execute() throws Exception {
		trace();
		try {
			//getProblem().log("Post " + constraint.getName());
			constraint.post();
		} catch (Exception e) {
			throw (Exception)e.getCause(); //new Exception("Failure to post GoalConstrant "+constraint.getName());
		}
		return null;
	}

}
