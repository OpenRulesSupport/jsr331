//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search.goal;

import java.util.ArrayList;

import javax.constraints.Solver;
import javax.constraints.ValueSelector;
import javax.constraints.Var;
import javax.constraints.VarSelector;
import javax.constraints.impl.search.selectors.ValueSelectorMin;
import javax.constraints.impl.search.selectors.VarSelectorInputOrder;

public class GoalAssignValuesTimeLimit extends Goal {
	Var[] 			vars;
	VarSelector 	varSelector;
	ValueSelector 	valueSelector;
	Goal[] 			goals;
	Goal			checkGoal;

	public GoalAssignValuesTimeLimit(Var[] vars, VarSelector varSelector,
			ValueSelector valueSelector) {
		super(vars[0].getProblem().getSolver(), "AssignValuesWithTimeLimit");
		init(vars,varSelector,valueSelector);
	}
	
	public GoalAssignValuesTimeLimit(Solver solver, Var[] vars) {
		super(solver,"AssignValuesTimeLimit");
		init(vars,new VarSelectorInputOrder(this),new ValueSelectorMin());
	}
	
	public GoalAssignValuesTimeLimit(Solver solver) {
		super(solver,"AssignValuesTimeLimit");
		init(solver.getProblem().getVars(),
			new VarSelectorInputOrder(this),
			new ValueSelectorMin()
			);
	}
	
	public void init(Var[] vars, VarSelector varSelector,
			ValueSelector valueSelector) {
		this.vars = vars;
		this.varSelector = varSelector;
		this.valueSelector = valueSelector;
		checkGoal = new GoalCheckTimeLimit(getSolver());
		createGoals(vars);
	}
	
	public void createGoals(Var[] vars) {
		goals = new Goal[vars.length];
		for (int i = 0; i < vars.length; i++) {
			//goals[i] = new GoalAssignValueTimeLimit(vars[i], valueSelector);
			goals[i] = new GoalAssignValue(vars[i], valueSelector);
		}
		checkGoal = new GoalCheckTimeLimit(getSolver());
	}
	
	

	@Override
	public void setVars(Var[] vars) {
		createGoals(vars);
	}

	@Override
	public void setVars(ArrayList<Var> varList) {
		Var[] vars = new Var[varList.size()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = varList.get(i);
		}
		createGoals(vars);
	}

	public Goal execute() throws Exception {
		trace();
		int index = varSelector.select();
		if (index == -1)
			return null;
		boolean timeLimitViolated = getSolver().checkTimeLimit();
		if (timeLimitViolated) {
			String msg = "Time limit " + solver.getTimeLimit() + 
					" has been exceeded while instantiating an array of " + vars.length + " variables " +
					vars[0].getName() + " ... "+ vars[vars.length - 1].getName();
			solver.getProblem().log(msg);
			return null;
			//return checkGoal; // if time limit is violated some vars would not be bound
		}
		//return goals[index].and(checkGoal).and(this);
		return goals[index].and(this);
	}
}
