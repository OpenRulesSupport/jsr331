//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Problem;

abstract public class AbstractConstrainedVariable extends CommonBase implements ConstrainedVariable {
	
	public AbstractConstrainedVariable(Problem problem) {
		this(problem,"");
	}
	
	public AbstractConstrainedVariable(Problem problem, String name) {
		super(problem,name);
	}
	
//	/**
//	 * This is a stub for the method "addPropagator" that
//	 * should be defined on the reference implementation level
//	 */
//	public void addPropagator(Propagator propagator, PropagationEvent event) {
//		throw new RuntimeException("The method addPropagator(..) is not defined for this type of constrained variables");
//	}

}
