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

public class GoalAssignValues extends Goal {
//	Var[] 				vars;
//	VarSelector 		varSelector;
//	ValueSelector 		valueSelector;
	protected Goal[] 	goals;
	
	/**
	 * Creates a Goal that instantiates (assign values to) all variables inside the
	 * array "vars". The "varSelector" defines an order in which the variables
	 * from "vars" will be selected to be instantiated. The "valueSelector"
	 * defines an order in which the values of a variable will be selected to be
	 * assigned.
	 * 
	 * @param vars
	 *            the array of Var variables to be enumerated.
	 * @param varSelector
	 *            a variable selector, defining the order in which to
	 *            instantiate the variables.
	 * @param valueSelector
	 *            a value selector, defining the order in which to assign the
	 *            values.
	 */
	public GoalAssignValues(Var[] vars, 
			            	VarSelector varSelector,
			            	ValueSelector valueSelector) {
		super(vars[0].getProblem().getSolver(),"AssignValues");
		setVarSelector(varSelector);
		setValueSelector(valueSelector);
		setVars(vars);
	}
	
	/**
	 * This is equivalent to GoalAssignValues(solver, solver.getProblem().getVars());
	 * @param solver Solver
	 */
	public GoalAssignValues(Solver solver) {
		this(solver, solver.getProblem().getVars());
	}
	
	
	public GoalAssignValues(Solver solver, Var[] vars) {
		super(solver,"AssignValues");
		setVarSelector(new VarSelectorInputOrder(this));
		setValueSelector(new ValueSelectorMin());
		setVars(vars);
	}
	
	public void setGoals(Var[] vars) {
		if (vars == null)
			return;
		goals = new Goal[vars.length];
		ValueSelector valueSelector = getValueSelector();
		for(int i=0; i<vars.length; i++) {
			goals[i] = new GoalAssignValue(vars[i],valueSelector);
		}
	}
	
	public void setVars(Var[] vars) {
		this.vars = vars;
		setGoals(vars);
	}
	
	@Override
	public void setValueSelector(ValueSelector valueSelector) {
		this.valueSelector = valueSelector;
		if (vars != null)
			setGoals(vars);
	}
	
	@Override
	public void setVars(ArrayList<Var> varList) {
		Var[] vars = new Var[varList.size()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = varList.get(i);
		}
		setGoals(vars);
	}
	
//	/**
//	 * Returns a type of this Search Strategy
//	 * @return string
//	 */
//	public String getType() {
//		return "GoalAssignValues";
//	}
	
	/**
	 * This is equivalent to GoalAssignValues(vars,varSelector,new ValueSelectorMin());
	 */
	public GoalAssignValues(Var[] vars,
            				VarSelector varSelector) {
		this(vars,varSelector,new ValueSelectorMin());
	}

//	/**
//	 * This is equivalent to GoalAssignValues(vars,new VarSelectorUnbound(vars));
//	 */
//	public GoalAssignValues(Var[] vars) {
//		this(vars,new VarSelectorInputOrder(this));
//	}
	
//	/**
//	 * This is equivalent to 
//	 * GoalAssignValues(vars,new VarSelectorUnbound(vars),valueSelector);
//	 */
//	public GoalAssignValues(Var[] vars,
//            				ValueSelector valueSelector) {
//		this(vars,new VarSelectorInputOrder(this),valueSelector);
//	}



	public Goal execute() throws Exception {
		trace();
	    int index = getVarSelector().select();
	    if (index == -1)
	      return null; // all vars are instantiated

	    Goal goal = goals[index];

	    return goal.and(this);
	}


}
