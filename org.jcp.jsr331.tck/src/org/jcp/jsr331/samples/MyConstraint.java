package org.jcp.jsr331.samples;

import javax.constraints.Constraint;
import javax.constraints.Var;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;
import javax.constraints.impl.AbstractConstraint;
import javax.constraints.impl.Problem;
import javax.constraints.impl.AbstractVar;

/**
 * This is an example of how to write a custom constraint without necessity to go to 
 * a reference implementation level.
 * The MyConstrant implements "X <= Y + A". The propagation logic:
 * <ul>
 * <li> If Min of X is changed to XMIN, Then Y should be more or equal of XMIN - A
 * <li> If Max of Y is changed to YMAX, Then X should be less or equal of YMAX + A
 * </ul>
 *
 */

public class MyConstraint extends AbstractConstraint {
	
	AbstractVar x,y;
	int a;
	Propagator propagatorX, propagatorY;
	
	public MyConstraint(Var x, Var y, int a) {
		super(x.getProblem(),"MyConstraint");
		this.x = (AbstractVar)x;
		this.y = (AbstractVar)y;
		this.a = a;
		propagatorX = new PropagatorX();
		propagatorY = new PropagatorY();
	}
	
	/**
	 * This methods does initial constraint propagation and 
	 * subscribes propagators to the events for the proper variables
	 */
	public void post() {
		try {
			propagatorX.propagate(PropagationEvent.MIN);
			x.addPropagator(propagatorX, PropagationEvent.MIN);
			propagatorY.propagate(PropagationEvent.MAX);
			y.addPropagator(propagatorY, PropagationEvent.MAX);
			
		} catch (Exception e) {
			x.getProblem().log("Error while posting MyConstraint");
		}
	}

	/**
	 * This class defines a propagator for X:
	 * If Min of X is changed to XMIN, 
	 * Then Y should be more or equal of XMIN - A
	 */
	class PropagatorX implements Propagator {
		public void propagate(PropagationEvent event) throws Exception {
			javax.constraints.Problem p = x.getProblem();
			p.log("PropagatorX: "+event);
			if (event.equals(PropagationEvent.MIN))
				p.post(y,">=",x.getMin()-a); // not y.setMin(x.getMin()-a);
		}
	}
	
	/**
	 * This class defines a propagator for Y:
	 * If Max of Y is changed to YMAX, 
	 * Then X should be less or equal of YMAX + A
	 */
	class PropagatorY implements Propagator {
		public void propagate(PropagationEvent event) throws Exception {
			javax.constraints.Problem p = x.getProblem();
			p.log("PropagatorY: "+event);
			if (event.equals(PropagationEvent.MAX))
				p.post(x,"<=",y.getMax()+a); // not x.setMax(y.getMax()+a);
		}
	}
	
	public static void main(String[] args) {
		Problem problem = new Problem("TestMyConstraint");
		Var x = problem.variable("X", 0, 15);
		Var y = problem.variable("Y", -5, 10);
		Constraint my = new MyConstraint(x, y, 3);
		problem.log("Before MyConstraint Posting",problem.getVars());
		my.post();
		try {
			problem.log("After MyConstraint Posting",problem.getVars());
			problem.post(x,">=",4); // x.setMin(4);
			problem.log("After X >= 4",problem.getVars());
			problem.post(y,"<=",5); // y.setMax(5);
			problem.log("After Y <= 9",problem.getVars());
			
		} catch (Exception e) {
			problem.log("error in modifiers");
		}
	}
}
