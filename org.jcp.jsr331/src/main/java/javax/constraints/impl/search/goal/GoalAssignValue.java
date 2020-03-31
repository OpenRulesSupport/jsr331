//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search.goal;

import javax.constraints.Problem;
import javax.constraints.ValueSelector;
import javax.constraints.Var;
import javax.constraints.impl.search.selectors.ValueSelectorMin;

public class GoalAssignValue extends Goal {
	protected Var var;
//	ValueSelector valueSelector;

	public GoalAssignValue(Var var, ValueSelector valueSelector) {
		super(var.getProblem().getSolver(), "AssignValue to " + var);
		this.var = var;
		setValueSelector(valueSelector);
	}

	public GoalAssignValue(Var var) {
		this(var,new ValueSelectorMin());
	}

	public Goal execute() throws Exception {
		Problem p = getProblem();
		trace();
		if (var.isBound())
			return null;

		int value = getValueSelector().select(var);

//		p.log("try to assign value " + value + " to " + var);
//		Constraint assign = p.linear(var, "=", value);
//		Goal goalAssignValue = new GoalConstraint(assign);
//		goalAssignValue.setName("Assign " + value + " to " + var);
//		Constraint remove = p.linear(var, "!=", value);
//		Goal goalRemoveValue = new GoalConstraint(remove);
//		goalRemoveValue.setName("Remove " + value + " from " + var);
		Goal goalAssignValue = getSolver().goalVarEqValue(var, value);
		Goal goalRemoveValue = getSolver().goalVarNeqValue(var, value);
		return goalAssignValue.or(goalRemoveValue.and(this));
	}

}
