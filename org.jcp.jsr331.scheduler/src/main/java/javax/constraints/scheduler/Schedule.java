//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler;

/**
 * A scheduling problem can be viewed as a constraint satisfaction problem
 * defined by:
 * <ul>
 * <li> a set of activities   tasks or work to be completed;
 * <li> a set of temporal constraints   relationships between the start
 * and end times of the activities;
 * <li> a set of resources   objects such as workers, machines, vehicles,
 * supplies, raw materials, etc., which add value to a product or service
 * in its creation, production, or delivery;
 * <li> a set of resource constraints   relationships between activities
 * and resources, such as "activity requires resource with a certain daily capacity".
 *
 * Schedule may be considered as a factory and a placeholder for different 
 * activities, resources and constraints between them. 
 *
 * @see Activity
 * @see Resource
 */

import java.util.Vector;

import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;


public interface Schedule extends Problem {

	/**
	 * Adds activity to the vector of all problem activities.
	 * @param activity
	 * @return an added activity
	 */
	public Activity add(Activity activity);

	/**
	 * Return a duration (horizon) of this schedule that is calculated as 
	 * getEnd() - getStart(). 
	 * @return an integer value
	 */
	public int getDuration();

	/**
	 * @return a vector of all added activities
	 */
	public Vector<Activity> getActivities();
	
	/**
	 * 
	 * @param name
	 * @return an activity with a given name
	 */
	public Activity getActivity(String name);

	/**
	 * Returns the end of the schedule, e.g. the last day or hour. The end is not included into considered
	 * time units, e.g, a schedule can be defined at the interval [0;30) starts on the day 0, lasts
	 * 30 days and actually ends at the day 29. The end of this schedule is 30.
	 * @return the end of the schedule
	 */
	public int getEnd();
	
	/**
	 * Sets the end of this schedule
	 * @param end
	 */
	public void setEnd(int end);

	/**
	 * Returns a vector of all added resources
	 * @return a vector
	 */
	public Vector<Resource> getResources();

	/**
	 * Returns a resource with a given name
	 * @param name
	 * @return a found resource or null
	 */
	public Resource getResource(String name);

	/**
	 * returns a start of the schedule, its first day, hour, or  minute.
	 * @return an integer
	 */
	public int getStart();
	
	/**
	 * Sets the start of this schedule, The default start is 0.
	 * @param start
	 */
	public void setStart(int start);


	/**
	 * Creates and returns an activity "name" that starts at "from" and
	 * ends at "to" without interruptions.
	 * @param from
	 * @param to
	 * @param name
	 * @return
	 */
	public Activity activity(String name, int from, int to);
//	public Activity activity(String name, int from, int to, int duration);
//	public Activity activity(String name, int from, int to, Var duration);
	/**
	 * Creates and returns an activity "name" that lasts "duration" without interruptions.
	 * Start and end of the activity is defined the Schedule.
	 * @param from
	 * @param to
	 * @param name
	 * @return an activity
	 */
	public Activity activity(String name,int duration);
	
	/**
	 * Creates and returns an activity "name".
	 * @param name
	 * @return an activity
	 */
	public Activity activity(String name);
	
	/**
	 * Creates and returns a resource "name" with maximal capacity
	 * equal to "capacityMax". The default type of the resource is 
	 * ResourceType.DISCRETE
	 * @param name
	 * @param capacityMax
	 * @return a resource
	 */
	public Resource resource(String name,int capacityMax);
	
	/**
	 * Creates and returns a resource "name" with maximal capacity
	 * equal to "capacityMax". The type of the resource is defined by the
	 * parameter type 
	 * @param name
	 * @param capacityMax
	 * @param type
	 * @return a resource
	 */
	public Resource resource(String name,int capacityMax, ResourceType type);
	
	/**
	 * Creates and returns a discrete resource "name" with capacity 1
	 * @param capacityMax
	 * @param name
	 * @return resource
	 * @throws Exception
	 */
	public ResourceDisjunctive resourceDisjunctive(String name);

	/**
	 * Return a strategy that places all problem's activities in time by 
	 * instantiating their start times
	 * @return SearchStrategy
	 */
	public SearchStrategy strategyScheduleActivities();

	/**
	 * Returns a strategy that assigns all problem's resources to the activities 
	 * that require some capacities from these resources
	 * @return SearchStrategy
	 */
	public SearchStrategy strategyAssignResources();
	
	/**
	 * Returns a strategy that assigns problem's resources only activities which:
	 * 1) require some capacities from these resources
	 * 2) are already bound and their start time in equal to "time"
	 * @param time
	 * @return SearchStrategy
	 */
	public SearchStrategy strategyAssignResources(int time);
	
	
	/**
	 * Returns a vector of constrained integer variables which were
	 * added directly to this schedule using the Schedule's method add(Var)
	 * @return a vector of Var(s)
	 */
	public Vector<Var> getScheduleVars();
	
	/**
	 * Returns a strategy that instantiates all variables
	 * added directly to this schedule using the Schedule's method add(Var)
	 * @return SearchStrategy
	 */
	public SearchStrategy strategyScheduleVars();

	/**
	 * Schedules all activities by executing "strategyScheduleActivities".
	 * @return Solution if a solution is found
	 * @return null if cannot schedule at least one activity
	 */
	public Solution scheduleActivities();
	
	/**
	 * @return true if all activities (their start time variables) are bound
	 */
	public boolean areAllActivitiesBound();
	
	/**
	 * Schedules all activities by executing "strategyScheduleActivities"
	 * and assigns all resources that were required by activities with 
	 * unknown capacities by executing the strategy "strategyAssignResources".
	 * @return Solution if a solution is found
	 * @return null if cannot schedule at least one activity
	 */
	public Solution scheduleActivitiesAndAssignResources();

	/**
	 * Assigns all resources that were required by activities with unknown capacities
	 * by executing the strategy "strategyAssignResources".
	 * @return Solution if a solution is found
	 * @return null if cannot schedule at least one activity or assign a resource
	 */
	public Solution assignResources();

	/**
	 * Log all activities
	 */
	public void logActivities();

	/**
	 * Log all resources
	 */
	public void logResources();
	
	/**
	 * Log a solution
	 * @param solution
	 */
	public void log(Solution solution);
	
	/**
	 * Save a solution back to activities, resources, and schedule variables
	 * @param solution
	 */
	public void save(Solution solution);
	
	// Temporal and Precedence constraints
	
	/**
	 * Posts a constraint such as "act1 > act2" that means that
	 * activity "act1" starts after the end of activity "act2". There could be a time offset
	 * that allows to adjust relationships between activity starts and ends. Here are examples 
	 * of the precedence constraints and their meanings:
	 * <ul>
	 * <li> post(act1,">",act2) - act1 starts after the end of act2
	 * <li> post(act1,">",act2,10) - act1 starts 10 minutes (hours, ...) after the end of act2
	 * <li> post(act1,">",act2,-10) - act1 starts 10 minutes (hours, ...) before the end of act2
	 * <li> post(act1,">=",act2) - act1 starts after the start of act2
	 * <li> post(act1,"=",act2) - act1 and act2 starts at the same time
	 * <li> post(act1,"<=",act2) - the same time post(act2,">=",act1)
	 * <li> post(act1,"<",act2) - the same time post(act2,">",act1)
	 * </ul>
	 * @return a posted constraint
	 */
	public Constraint post(Activity act1, String oper, Activity act2, int offset);
	
	/**
	 * Posts a constraint such as "act1 > act2" that means that
	 * activity "act1" starts after the end of activity "act2".
	 * This is a special case of the previous constraint with offset = 0
	 */
	public Constraint post(Activity act1, String oper, Activity act2);
	
	/**
	 * Posts a constraint such as "act1 > 5" that means that
	 * activity "act1" starts after 5. Here are examples 
	 * of the temporal constraints and their meanings:
	 * <ul>
	 * <li> post(act1,">",10) - act1 starts after 10
	 * <li> post(act1,">=",10) - act1 starts at or after 10
	 * <li> post(act1,"=",10) - act1 starts exactly at 10
	 * <li> post(act1,"<=",10) - act1 starts at or before 10
	 * <li> post(act1,"<",10) - act1 starts before 10
	 * </ul>
	 * @return a posted constraint
	 */
	public void post(Activity act1, String oper, int time);
	
	/**
	 * Creates and posts a constraint that all activities in the array "activities)
	 * do not intersect
	 * @param activities
	 * @return a posted constraint
	 */
	public void postAllDiff(Activity[] activities);
	
//	/**
//	 * Saves the solution inside activities and resources
//	 * @param solution
//	 */
//	public void save(Solution solution);
	
	/**
	 * 
	 * @param resource
	 * @return an array of constrained variables that represent capacities of all constraints
	 * in which this resource participates
	 */
	public Var[] getConstraintCapacites(Resource resource);

}
