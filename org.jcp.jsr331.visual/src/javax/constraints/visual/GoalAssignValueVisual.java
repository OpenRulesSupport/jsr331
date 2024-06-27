//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.visual;

import javax.constraints.Problem;
import javax.constraints.Solver;
import javax.constraints.ValueSelector;
import javax.constraints.Var;
import javax.constraints.impl.search.goal.Goal;
import javax.constraints.impl.search.goal.GoalAssignValue;
import javax.constraints.impl.search.selectors.ValueSelectorMin;
import javax.constraints.visual.Nodes.Node;

public class GoalAssignValueVisual extends GoalAssignValue {

	public GoalAssignValueVisual(Var var, ValueSelector valueSelector) {
		super(var,valueSelector);
		setName("AssingValueVisual to "+var.toString());
	}

	public GoalAssignValueVisual(Var var) {
		super(var);
		setName("AssingValueVisual to "+var.toString());
	}
	
	public SolverVisual solver() {
		return (SolverVisual)getSolver();
	}

	public Goal execute() throws Exception {
		Problem p = getProblem();
		trace();
		if (var.isBound()) {
//			tree.addChild(var, var.getValue(), "v");
			return null;
		}

		int value = getValueSelector().select(var);

		Node node = solver().nodes().addNode();
		int id = node.getId();
		int parentId = node.getParentId();
		p.log("Try " + var.toString() + " with " + value
			  +	" id=" + id + " parentId=" + parentId);
		
		Goal goalAssignValue = getSolver().goalVarEqValue(var, value);
		
		Goal successGoal = new GoalAddSuccessNode(var, var.getDomainSize(),value);
		Goal goal1 = goalAssignValue.and(successGoal);
		Goal goalRemoveValue = getSolver().goalVarNeqValue(var, value);
		Goal failureGoal = new GoalAddFailureNode(var, var.getDomainSize(),value);
		Goal goal2 = failureGoal.and(goalRemoveValue);
		return goal1.or(goal2.and(this));
	}
	
	class GoalAddSuccessNode extends Goal {
		Var var;
		int size;
		int value;

		public GoalAddSuccessNode(Var var, int size, int value) {
			super(var.getProblem().getSolver(),"AddSuccessNode");
			this.var = var;
			this.size = size;
			this.value = value;
		}

		public Goal execute() throws Exception {
			Nodes nodes = solver().nodes();
			Node node = nodes.getCurrent();
//			Node node = solver().nodes().addNode();
			getProblem().log("SUCCESS("+node.getId()+","+node.getParentId()+","
					+var.toString()+","+size+","+value+")");
			SolverVisual solver = (SolverVisual)getSolver();
			solver.addSuccessNode(node.getId(), node.getParentId(), var.getName(), size, value);
			return null;
		}
	}
	
	class GoalAddFailureNode extends Goal {
		Var var;
		int size;
		int value;

		public GoalAddFailureNode(Var var, int size, int value) {
			super(var.getProblem().getSolver(),"AddFailureNode");
			this.var = var;
			this.size = size;
			this.value = value;
		}

		public Goal execute() throws Exception {
//			Nodes nodes = solver().nodes();
//			if (nodes.isEmpty())
//				return null;
//			Node node = nodes.getCurrent();
//			int id = nodes.incrCurrentId();
			Nodes nodes = solver().nodes();
			Node node = nodes.getCurrent();
			if (node.getId() == nodes.getCurrentId()) {
				getProblem().log("FAILURE("+node.getId()+","+node.getParentId()+","
					+var.toString()+","+size+","+value+")");
				solver().addFailureNode(node.getId(),node.getParentId(), var.getName(), size, value);
			}
			solver().nodes().removeNode();
			if (node.getId() == nodes.getCurrentId()) {
				// Create a remove value node
				if (!nodes.isEmpty()) {
					Node current = nodes.getCurrent();
					int id = nodes.incrCurrentId();
					String strValue = var.getName()+"!="+value;
					getProblem().log("TRY("+id+","+current.getId()+","
							+var.toString()+","+size+","+strValue+")");
					solver().addSuccessNode(id,current.getId(), "", size, strValue);
				}
			}
			return null;
		}
	}

}
