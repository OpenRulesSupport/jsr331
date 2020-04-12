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
 * The interface Constraint defines a generic interface for all
 * constraints that could be created and posted within the Problem.
 * Concrete constraints can be defined in two ways:
 * <ul>
 * <li> explicitly in the interface Problem such as "allDifferent"</li>
 * <li> implicitly inside constrained expressions such as var1.eq(var2). </li>
 * </ul>
 * <br>
 * The interface Problem includes only commonly used and de-facto standardized
 * global constraints in their most popular forms. This API assumes that there
 * will be a library of global constraints that implements the interface Constraint 
 * and follows the guidelines of the  
 * <a href="http://www.emn.fr/x-info/sdemasse/gccat/index.html">Global Constraint Catalog</a>.
 * <br>
 *
 */

public interface Constraint {
	
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

	/**
	 * This method is used to post the
	 * constraint. The constraint posting may do two things:
	 * 1) initial constraint propagation will be executed;
	 * 2) the constraint will be memorized and wait for notification events
	 * when the variables it controls are changed.
	 * The actual posting logic depends on an underlying CP solver.
	 * If the posting was unsuccessful, this method throws a runtime exception.
	 * A user may put constraint posting in a try-catch block to react to
	 * posting failures.
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post();
	
	/**
	 * This method is used to post the constraint. Additionally to post() 
	 * this methods specifies a particular level of consistency that will
	 * be selected by an implementation to control the propagation strength of
	 * this constraint.
	 * @param consistencyLevel ConsistencyLevel
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(ConsistencyLevel consistencyLevel);
	
	/**
	 * This method is used to post the constraint assuming that it can be violated. 
	 * The parameter probability defines a probability that this constraint 
	 * will not be violated. If it's ALWAYS this is the same as a regular constraint posting.
	 * If it's NEVER, the posting of this constraint will fail producing a RuntimeException. 
	 * All other values allow the constraint to be violated with certain penalties. 
	 * For instance, probability LOW invokes higher penalty to compare with probability HIGH, but 
	 * a lower penalty to compare with VERY_LOW. 
	 * The Problem' method getTotalConstraintViolation() returns a variable that could be minimized
	 * to find a solution that may all synchronize all relative constraint violations. 
	 * Thus, posting related constraints with different probabilities
	 * may resolve their conflicts.
	 * @param name of the constraint
	 * @param probability Probability
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(String name, Probability probability);

	/**
	 * This method returns a Var variable that is equal 1 if the constraint
	 * is satisfied and equals 0 if it is violated.
	 * @return Var with a domain [0;1]: a 1 value indicates the constraint is satisfied,
	 *                                  a 0 value indicates the constraint is violated.
	 */
	public VarBool asBool();
	
	/**
	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if both of the
	 * 		   Constraints "this" and "c" are satisfied. The Constraint "AND" is not satisfied
	 * 		   if at least one of the Constraints "this" or "c" is not satisfied.
	 * @param c the Constraint which is part of the new "AND" Constraint
	 * @return a Constraint "AND" between the Constraints "this" and "c2".
	 */
	public Constraint and(Constraint c);

	/**
	 * Returns an "OR" Constraint. The Constraint "OR" is satisfied if either of the
	 * 		   Constraints "this" and "c" is satisfied. The Constraint "OR" is not satisfied
	 * 		   if both of the Constraints "this" and "c" are not satisfied.
	 * @param c the Constraint which is part of the new "OR" Constraint
	 * @return a Constraint "OR" between the Constraints "this" and "c".
	 */
	public Constraint or(Constraint c);

	/**
	 * Returns a Constraint that is satisfied if and only if this constraint is not satisfied.
	 * @return a Constraint that is satisfied if and only if this constraint is not satisfied.
	 */
	public Constraint negation();
	
	/**
	 * Returns a Constraint that states the implication: if this then c.
	 * In other words, if this constraint is satisfied, then constraint "c"
	 * should also be satisfied.
	 *
	 * @param c the Constraint in the implication.
	 * @return a Constraint that means 'if this then c'.
	 */
	public Constraint implies(Constraint c);
		
}
