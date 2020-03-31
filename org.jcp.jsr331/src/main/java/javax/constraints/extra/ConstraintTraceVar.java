//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.extra;


import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.impl.AbstractConstraint;

/**
 * This constraint allows a user to trace a variable logging a given event every time
 * when it occurs
 *
 */
public class ConstraintTraceVar extends AbstractConstraint {

	Var var;
	PropagationEvent event;
	Propagator propagator;

	public ConstraintTraceVar(Var var, PropagationEvent event) {
		super(var.getProblem());
		this.var = var;
		this.event = event;
		this.propagator = new MyPropagator();
	}

	/**
	 * This methods subscribes MyPropagator to the "event" for the variable "var"
	 */
	public void post() {
		((javax.constraints.impl.AbstractVar)var).addPropagator(propagator, event);
	}

	/**
	 * This class defines a simple Propagator that logs an event every time
	 * when its method "propagate" is invoked
	 *
	 */
	class MyPropagator implements Propagator {
		public void propagate(PropagationEvent event) throws Exception {
			var.getProblem().log(event + ": " + var);
		}
	}
	
	/**
	 * @return a VarBool variable that is equal to 1
	 */
	public VarBool asBool() {
		Problem p = getProblem();
		VarBool var = p.variableBool();
		p.post(var, "=", 1);
		return var;
	}

}
