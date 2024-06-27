//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.visual;

import javax.constraints.Solver;
import javax.constraints.ValueSelector;
import javax.constraints.Var;
import javax.constraints.VarSelector;
import javax.constraints.impl.search.goal.Goal;
import javax.constraints.impl.search.goal.GoalAssignValues;
import javax.constraints.impl.search.selectors.ValueSelectorMin;
import javax.constraints.impl.search.selectors.VarSelectorInputOrder;
import javax.constraints.visual.Nodes.Node;

public class GoalAssignValuesVisual extends GoalAssignValues {
	
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
	public GoalAssignValuesVisual(Var[] vars, 
			            	VarSelector varSelector,
			            	ValueSelector valueSelector) {
		super(vars,varSelector,valueSelector);
		init();
	}
	
	void init() {
		setName("AssingValuesVisual");
//		int id = solver().getCurrentNodeId(); // should be 0
//		Node rootNode = new Node(id,0);
//		setBusinessObject(rootNode);
	}
	
	/**
	 * This is equivalent to GoalAssignValues(solver, solver.getProblem().getVars());
	 */
	public GoalAssignValuesVisual(Solver solver) {
		super(solver);
		init();
	}
	
	
	public GoalAssignValuesVisual(Solver solver, Var[] vars) {
		super(solver,vars);
		init();
	}
	
	public GoalAssignValuesVisual(Var[] vars,
            				VarSelector varSelector) {
		super(vars,varSelector);
		init();
	}
	
	public SolverVisual solver() {
		return (SolverVisual)getSolver();
	}
	
	@Override
	public void setGoals(Var[] vars) {
		goals = new GoalAssignValueVisual[vars.length];
		ValueSelector valueSelector = getValueSelector();
		for(int i=0; i<vars.length; i++) {
			goals[i] = new GoalAssignValueVisual(vars[i],valueSelector);
		}
	}
	
	/**
	 * Returns a type of this Search Strategy
	 * @return string
	 */
	public String getType() {
		return "GoalAssignValuesVisual";
	}
	
	public Goal execute() throws Exception {
		trace();
	    int index = getVarSelector().select();
	    if (index == -1) {
	    	Nodes nodes = solver().nodes();
	    	int id = nodes.getCurrentId();
	    	if (!nodes.isEmpty()) {
	    		id = nodes.getCurrent().getId();
	    	}
			solver().labelSolutionNode(id); 
			solver().log("SOLUTION node=" + id);
	        return null; // all vars are instantiated
	    }

	    //getProblem().log("Var " + getVars()[index] + " is chosen");
	    Goal goal = goals[index];
	    return goal.and(this);
	}


}
