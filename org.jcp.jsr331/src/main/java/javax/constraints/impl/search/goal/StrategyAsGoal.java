package javax.constraints.impl.search.goal;

import javax.constraints.Problem;
import javax.constraints.SearchStrategy;
import javax.constraints.Solver;


public class StrategyAsGoal extends Goal {
	
	SearchStrategy strategy;

	public StrategyAsGoal(Solver solver, SearchStrategy strategy) {
		super(solver,strategy.getName());
		this.strategy = strategy;
	}

	public Goal execute() throws Exception {
		Problem p = strategy.getSolver().getProblem();
		try {
			strategy.run();
			return null;
		}
		catch (Exception e) {
			String msg = "Unexpected exception during execution of goal '" + strategy.getName()+"'";
			p.log(msg);
			p.log(e.toString());
			e.printStackTrace();
			throw new RuntimeException(msg);
		}
	}

}
