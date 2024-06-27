package javax.constraints.visual;

import javax.constraints.Var;
import javax.constraints.impl.constraint.AllDifferent;

public class AllDifferentVisual extends AllDifferent {
	
	Var[] vars;

	public AllDifferentVisual(Var[] vars){
		super(vars);
		this.vars = vars;
		ProblemVisual p = (ProblemVisual)getProblem();
		p.register(this);
	}
	
	public void snapshot(ProblemVisual problem) {
		int n = vars.length;
		for(int i=0; i < n; i++){
			problem.tagVariable(i,vars[i]);
		}
	}
}
