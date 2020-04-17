//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler;

import java.util.Vector;

import javax.constraints.Constraint;

/**
 * At any given time, a resource may or may not be allowed 
 * to be in a given state as defined by a user.
 * The interface ResourceState represents a resource of infinite capacity.
 * The state of this resource can vary over time. 
 * Activity may require a state resource to be in a given state 
 * (or in any of a given set of states). Two activities may not overlap 
 * if they require incompatible states during their execution. 
 * Activities also may provide a given state(s) of the resource. 
 * 
 */

public interface ResourceState extends Resource {
	/**
	 * @return a vector of all ResourceConstraint objects
	 * associated with this resource
	 */
	public Vector<Constraint> getActivityConstraints();
	
}
