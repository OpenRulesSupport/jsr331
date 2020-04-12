//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints.extra;

import java.util.EventListener;



/**
 * This interface represents propagators that used to 
 * propagate PropagationEvents when new(!) custom constraints
 * are defined. It does NOT specify how to implement actual propagators 
 * (a.k.a observers, listeners, or just constraints) inside reference implementations.
 */

public interface Propagator extends EventListener {
	/**
	 * Describes the propagation logic of the occurred event.
	 * @param event the occurred event whose propagation logic is specified in this method.
	 */
	public void propagate(PropagationEvent event) throws Exception;

}
