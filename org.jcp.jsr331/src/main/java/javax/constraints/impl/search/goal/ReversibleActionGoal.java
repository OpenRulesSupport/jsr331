package javax.constraints.impl.search.goal;

import javax.constraints.extra.ReversibleAction;

public class ReversibleActionGoal implements ReversibleAction {
	
	Goal goal;
	
	public ReversibleActionGoal(Goal goal) {
		this.goal = goal;
	}

	@Override
	public boolean execute() throws Exception {
		boolean result = true;
		try {
			goal.execute();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public Goal getGoal() {
		return goal;
	}
	

}
