package javax.constraints.impl.search;

import javax.constraints.Solver;
import javax.constraints.Var;

public class StrategyLogVariables extends AbstractSearchStrategy {
	Var[] vars;
	
	public StrategyLogVariables(Var[] vars) {
		super(vars[0].getProblem().getSolver());
		this.vars = vars;
		setType(SearchStrategyType.CUSTOM);
	}
	
	public StrategyLogVariables(Solver solver) {
		super(solver);
		vars = getProblem().getVars();
		setType(SearchStrategyType.CUSTOM);
	}
	
	public boolean run() {
		getProblem().log("=== StrategyLogVariables:");
		getProblem().log(vars);
		return true;
	}

}
