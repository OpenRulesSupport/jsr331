//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//=============================================
package javax.constraints.impl.search.goal;

import javax.constraints.Solver;

public class GoalPrint extends Goal {

	String text;

	public GoalPrint(Solver solver, String text) {
		super(solver);
		this.text = text;
	}

	public Goal execute() throws Exception {
		getProblem().log(text);
		return null;
	}
}
