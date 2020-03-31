//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl;

import javax.constraints.Problem;

/**
 * This is a base implementation for all types of problem objects 
 * that refer to the problem and have an associated name, business objects, implementation, etc.
 *
 */

public class CommonBase { 

	Problem problem;
	String  name;
	String 	id;
	Object  impl;
	Object  businessObject;
	
	public CommonBase(Problem problem) {
		this(problem,"");
	}
	
	public CommonBase(Problem problem, String name) {
		this.problem = problem;
		this.name = name;
		impl = null;
		businessObject = null;
	}
	
	
	public Problem getProblem() {
		return problem;
	}
	
	/**
	 * Returns the name of this object.
	 * @return the name of this object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this object.
	 * @param name the name for this object.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the id of this object.
	 * @return the id of this object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of this object.
	 * @param id the id for this object.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This method should be defined by a concrete solver
	 * implementation. This method returns an Object that represents an actual
	 * implementation of this object inside an underlying solver.
	 * @return an Object that represents an actual implementation of this Constrained
	 *         Object inside an underlying solver.
	 */
	public final Object getImpl() {
		return impl;
	}

	/**
	 * This method defines an Object that represents an actual
	 * implementation of this object inside a concrete solver.
	 * @param impl the Object that represents an actual implementation of this
	 *             object inside a implementation solver.
	 */
	public final void setImpl(Object impl) {
		this.impl = impl;
	}

	/**
	 * This method may be used to attach a business object to this
	 * object.
	 * @param obj the business object being attached.
	 */
	public final void setObject(Object obj) {
		this.businessObject = obj;
	}

	/**
	 * This method may be used to get an attached Business Object for this
	 * object.
	 * @return the Business Object attached to this object.
	 */
	public final Object getObject() {
		return businessObject;
	}

}
