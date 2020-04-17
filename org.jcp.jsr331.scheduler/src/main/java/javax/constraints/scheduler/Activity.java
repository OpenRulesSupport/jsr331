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
import javax.constraints.Solution;
import javax.constraints.Var;

/**
 * This is an interface for scheduling activities. 
 * An activity executes without interruption from its start time to its end time.
 * An activity may require resources (recoverable or consumable).
 * 
 */

public interface Activity {

	/**
	 * 
	 * @return the name of this activity
	 */
	public String getName();
	/**
	 * Sets the name of this activity
	 * @param name
	 */
	public void setName(String name);

	/**
	 * An activity is always associated with a scheduling problem, an instance of the class Schedule.
	 * @return an associated schedule
	 */
	public Schedule getSchedule();

	/**
	 * 
	 * @return a duration of this activity if it is known. Throws a runtime exception otherwise.
	 */
	public int getDuration();

	/**
	 * 
	 * @return a constrained variable associated with the duration of this activity 
	 * or null if it was not defined
	 */
	public Var getDurationVar();

	/**
	 * 
	 * @return a constrained variable associated with the end of this activity 
	 * or null if it was not defined
	 */
	public Var getEnd();

	/**
	 * 
	 * @return current maximal end of this activity.
	 */
	public int getEndMax();

	/**
	 * 
	 * @return a constrained variable associated with the start of this activity 
	 * or null if it was not defined
	 */
	public Var getStart();

	/**
	 * 
	 * @return current minimal start of this activity.
	 */
	public int getStartMin();
	
	/**
	 * 
	 * @return true if activity's start and duration are known
	 */
	public boolean isBound();
	
	// Temporal and Precedence constraints
	
		/**
		 * Creates a constraint such as "act1 > act2" that means that
		 * activity "act1" starts after the end of activity "act2". There could be a time offset
		 * that allows to adjust relationships between activity starts and ends. Here are examples 
		 * of the precedence constraints and their meanings:
		 * <ul>
		 * <li> act1.start(">",act2) - act1 starts after the end of act2
		 * <li> act1.start(">",act2,10) - act1 starts 10 minutes (hours, ...) after the end of act2
		 * <li> act1.start(">",act2,-10) - act1 starts 10 minutes (hours, ...) before the end of act2
		 * <li> act1.start(">=",act2) - act1 starts after the start of act2
		 * <li> act1.start("=",act2) - act1 and act2 starts at the same time
		 * <li> act1.start("<=",act2) - the same time act2.start(">=",act1)
		 * <li> act1.start("<",act2) - the same time act2.start(">",act1)
		 * </ul>
		 * @return a constraint
		 */
		public Constraint start(String oper, Activity act2, int offset);
		
		/**
		 * Creates a constraint such as "act1 > act2" that means that
		 * activity "act1" starts after the end of activity "act2".
		 * This is a special case of the previous constraint with offset = 0
		 */
		public Constraint start(String oper, Activity act2);
		
		/**
		 * Creates a constraint such as "act1 > 5" that means that
		 * activity "act1" starts after 5. Here are examples 
		 * of the temporal constraints and their meanings:
		 * <ul>
		 * <li> act1.start(">",10) - act1 starts after 10
		 * <li> act1.start(">=",10) - act1 starts at or after 10
		 * <li> act1.start("=",10) - act1 starts exactly at 10
		 * <li> act1.start("<=",10) - act1 starts at or before 10
		 * <li> act1.start("<",10) - act1 starts before 10
		 * </ul>
		 * @return a constraint
		 */
		public Constraint start(String oper, int time);
		
		/**
		 * Creates a constraint that states that this activity is different (does not intersect)
		 * with the activity "act2"
		 * @param act2
		 * @return a constraint
		 */
		public Constraint diff(Activity act2);

	/**
	 * 
	 * @return a vector of resource constraints posted on this activity
	 */
	public Vector<ConstraintActivityResource> getResourceConstraints();

	/**
	 *  This method posts a constraint that states: this activity requires the given 
	 *  capacity of the given resource during every time unit from the beginning to 
	 *  the end of its execution. Based on a resource type (recoverable or consumable)
	 *  the required capacity may recover or not after the end of this activity.
	 *  
	 * @param resource
	 * @param capacity integer that represent required capacity per time unit (date, hour, etc.)
	 * @return the posted constraint
	 */
	public ConstraintActivityResource requires(Resource resource, int capacity);

	/**
	 *  This method posts a constraint that states: this activity requires a not-yet known 
	 *  capacity expressed as "capacityVar" of the given resource during every time unit 
	 *  from the beginning to 
	 *  the end of its execution. Based on a resource type (recoverable or consumable)
	 *  the required capacity may recover or not after the end of this activity.
	 *  
	 * @param resource
	 * @param capacity integer variable that represent required capacity per time unit (date, hour, etc.)
	 * @return the posted constraint
	 */
	public ConstraintActivityResource requires(Resource resource, Var capacityVar);

	/**
	 *  This method posts a constraint that states: this activity requires this boolean resource
	 *  all the time from the beginning to the end of its execution. 
	 *  
	 * @param resource
	 * @return the posted constraint
	 */
	public ConstraintActivityResource requires(ResourceDisjunctive resource);
	
		
	
	
	/**
	 * This method posts a constraint that states: this activity requires one and only one
	 * resource from the array of alternative resources.
	 * @param array of resources
	 * @return the posted constraint
	 */
	public Constraint requires(ResourceDisjunctive[] resources);
	
	/**
	 * This method posts a constraint that states: this activity requires
	 * either resource 1 or resource 2
	 * @param resource1
	 * @param resource2
	 * @return the posted constraint
	 */
	public Constraint requires(ResourceDisjunctive resource1, ResourceDisjunctive resource2);
	
	/**
	 * This method posts a constraint that states: this activity requires
	 * one and only one of 3 alternative resources
	 * @param resource1
	 * @param resource2
	 * @param resource3
	 * @return the posted constraint
	 */
	public Constraint requires(ResourceDisjunctive resource1, 
			                   ResourceDisjunctive resource2,
			                   ResourceDisjunctive resource3);

	
	/**
	 * This method posts a constraint that states: 
	 * this activity requires one and only one resource 
	 * from the pool of alternative disjunctive resources.
	 * @param pool of disjunctive resources
	 * @return the posted constraint
	 */
	public Constraint requires(ResourceDisjunctivePool pool);
	
	/**
	 * This method posts a constraint that states: 
	 * this activity requires one and only one resource 
	 * from the pool of alternative disjunctive resources.
	 * There is an assumption that all resources within the pool
	 * are ordered starting from 0. The parameter-variable indexVar serves 
	 * as an index within the "pool" that points to the actually assigned resource.
	 * @param pool of disjunctive resources
	 * @param indexVar a constrained variable 
	 * @return the posted constraint
	 */
	public Constraint requires(ResourceDisjunctivePool pool, Var indexVar);
	
	/**
	 * This method posts a constraint that states: 
	 * this activity requires a certain integer "capacity" 
	 * from the pool of alternative resources.
	 * @param pool of resources
	 * @param capacity int
	 * @return the posted constraint
	 */
	public Constraint requires(ResourcePool pool, int capacity);
	
	/**
	 * This method posts a constraint that states: 
	 * this activity requires certain variable "capacityVar" 
	 * from the pool of alternative resources.
	 * @param pool of resources
	 * @param capacityVar Var
	 * @return the posted constraint
	 */
	public Constraint requires(ResourcePool pool, Var capacityVar);
	
	/**
	 * This method posts a constraint that states: 
	 * this activity should not share resources with the activity "act"
	 * @param act
	 */
	public void doNotShareResourcesWith(Activity act);
	
	/**
	 * This method may be used to attach a business object to this
	 * activity.
	 * @param obj the business object being attached.
	 */
	public void setObject(Object obj);

	/**
	 * This method may be used to get an attached Business Object for this
	 * activity.
	 * @return the Business Object attached to this object.
	 */
	public Object getObject();
	

	/**
	 * @return a String representation of this activity
	 */
	public String toString();
	
	/**
	 * 
	 * @return a calculated start of this activity 
	 */
	public int getSolutionStart();
	
	/**
	 * 
	 * Sets a calculated start of this activity 
	 */
	public void setSolutionStart(int start);
	
	/**
	 * 
	 * @return a calculated end of this activity 
	 */
	public int getSolutionEnd();
	
	/**
	 * 
	 * Sets a calculated end of this activity 
	 */
	public void setSolutionEnd(int end);
	
//	/**
//	 * Sets solutionStart and solutionEnd for this solution
//	 * @param solution
//	 */
//	public void save(Solution solution);
}
