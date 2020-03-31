//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;


/**
 * This is a base interface for all constrained variable. It defines variable names,
 * a Problem to which the variable belongs to, an associated business object, etc. 
 *
 */

public interface ConstrainedVariable {

	/**
	 * Return a problem to which this object belongs
	 * @return a problem to which this object belongs
	 */
	public Problem getProblem();

	/**
	 * Returns the name of this object.
	 * @return the name of this object.
	 */
	public String getName();

	/**
	 * Sets the name of this object.
	 * @param name the name for this object.
	 */
	public void setName(String name);
	
	/**
	 * Returns the id of this object.
	 * @return the id of this object.
	 */
	public String getId();

	/**
	 * Sets the id of this object.
	 * @param id the id for this object.
	 */
	public void setId(String id);

	/**
	 * This method should be defined by a concrete solver
	 * implementation. This method returns an Object that represents an actual
	 * implementation of this object inside an underlying solver.
	 * @return an Object that represents an actual implementation of this Constrained
	 *         Object inside an underlying solver.
	 */
	public Object getImpl();

	/**
	 * This method defines an Object that represents an actual
	 * implementation of this object inside a concrete solver.
	 * @param impl the Object that represents an actual implementation of this
	 * object inside a implementation solver.
	 */
	public void setImpl(Object impl);

	/**
	 * This method may be used to attach a business object to this
	 * object.
	 * @param obj the business object being attached.
	 */
	public void setObject(Object obj);

	/**
	 * This method may be used to get an attached Business Object for this
	 * object.
	 * @return the Business Object attached to this object.
	 */
	public Object getObject();
	
//	/**
//	 * Returns a number of constraints that constrain this variable
//	 * @return a number of constraints that constrain this variable
//	 */
//	public int getNumberOfConstraints();
	
//	/**
//	 * This method associates a user0defined Propagator with an "event"
//	 * related to changes in the domain of this constrained variable.
//	 * It forces the solver to keep an eye on these events and invoke the
//	 * Propagator "propagator" when these events actually occur. When such events
//	 * occur, the Propagator's method propagate() will be executed.
//	 * 
//	 * @param propagator
//	 *            the Propagator we wish to associate with events on the
//	 *            variable.
//	 * @param event
//	 *            the events that will trigger the invocation of the
//	 *            Propagator.
//	 */
//	public void addPropagator(Propagator propagator, PropagationEvent event);

}
