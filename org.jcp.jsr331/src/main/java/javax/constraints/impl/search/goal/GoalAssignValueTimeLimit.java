package javax.constraints.impl.search.goal;

import javax.constraints.Constraint;

import javax.constraints.Problem;
import javax.constraints.Solver;
import javax.constraints.ValueSelector;
import javax.constraints.Var;

public class GoalAssignValueTimeLimit extends Goal {
	Var 			var;
	ValueSelector 	valueSelector;

	public GoalAssignValueTimeLimit(Var var,
			                   ValueSelector valueSelector) {
		super(var.getProblem().getSolver(),"AssignValueWithTimeLimit to " + var);
		this.var = var;
		this.valueSelector = valueSelector;
	}

	public Goal execute() throws Exception {
		Problem p = getProblem();
		trace();
		if (var.isBound())
			return null;

		SolverWithGoals solver = getSolver();
		if(solver.checkTimeLimit()){
			if( solver.isTimeLimitExceeded() == false )	{
				solver.setTimeLimitExceeded(true);
				p.log("Time limit " + solver.getTimeLimit() + 
						" has been exceeded while instantiating variable "+var.getName());
			}
			return null;
			//the goal GoalCheckTimeLimit should check if variables are bound
		}

	    int value = valueSelector.select(var);

	    Constraint assign = p.linear(var, "=", value);
		Goal goalAssignValue = new GoalConstraint(assign);
		Constraint remove = p.linear(var, "!=", value);
		Goal goalRemoveValue = new GoalConstraint(remove);
	    return goalAssignValue.or(goalRemoveValue.and(this));
	}

}
