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
import javax.constraints.Var;

public class GoalLog extends Goal {

	String text;
	Var    var;
	Var[]  vars;

	public GoalLog(Solver solver, String text) {
		super(solver, "log");
		this.text = text;
		var = null;
		vars = null;
	}

	public GoalLog(Var var) {
		this("", var);
	}

	public GoalLog(String text, Var var) {
		super(var.getProblem().getSolver(),"log");
		this.text = text;
		this.var = var;
		this.vars = null;
	}

	public GoalLog(Var[] vars) {
		this("",vars);
	}

	public GoalLog(String text, Var[] vars) {
		super(vars[0].getProblem().getSolver(),"log");
		this.text = text;
		this.var = null;
		this.vars = vars;
	}

	public Goal execute() throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("\n" + text);
		if (var != null) {
			buf.append(" " + var.toString());
		}
		else {
			if (vars != null) {
				for (int i = 0; i < vars.length; i++) {
					Var v = vars[i];
					buf.append(" " + v.toString());
				}
			}
		}
		getProblem().log(buf.toString());
		return null;
	}
}
