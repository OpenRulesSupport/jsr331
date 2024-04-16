//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

/**
 * Schedule - a placeholder for scheduling components:
 * <ol>
 * <li> Activities
 * <li> Resources
 * <li> Assignments of resources to activities
 * </ol>
 *
 * @see Activity
 * @see Resource
 */

import java.util.Vector;

import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.ProblemDelegator;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;
import javax.constraints.impl.search.selectors.VarSelectorRandom;
//import javax.constraints.impl.Problem;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.ConstraintActivityResource;
import javax.constraints.scheduler.ResourceDisjunctive;
import javax.constraints.scheduler.ResourceType;
import javax.constraints.scheduler.Schedule;

public class ScheduleImpl extends ProblemDelegator implements Schedule {

	int start;
	int end;
	Vector<Activity> activities;
	Vector<Resource> resources;
	Vector<ConstraintActivityResource> resourceConstraints;
	Vector<Var> scheduleVars;

	/**
	 * Constructor for a schedule with the earliest start time and the latest
	 * end time defined as parameters
	 * 
	 * @param name
	 * @param start
	 * @param end
	 */
	public ScheduleImpl(Problem problem, String name, int start, int end) {
		super(problem);
		setName(name);
		this.start = start;
		this.end = end;
		activities = new Vector<Activity>();
		resources = new Vector<Resource>();
		resourceConstraints = new Vector<ConstraintActivityResource>();
		scheduleVars = new Vector<Var>();
	}

	public ScheduleImpl(Problem problem, String name) {
		this(problem, name, 0, 100);
	}

	public ScheduleImpl(Problem problem) {
		this(problem, "ScheduleImpl");
	}

	public Activity add(Activity activity) {
		activities.addElement(activity);
		return activity;
	}

	public int getDuration() {
		return end - start;
	}

	public Vector<Activity> getActivities() {
		return activities;
	}

	public Activity getActivity(String name) {
		for (int i = 0; i < activities.size(); i++) {
			Activity act = activities.elementAt(i);
			if (act.getName().equals(name))
				return act;
		}
		return null;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public Vector<Resource> getResources() {
		return resources;
	}

	public Resource getResource(String name) {
		for (int i = 0; i < resources.size(); i++) {
			Resource r = (Resource) resources.elementAt(i);
			if (r.getName().equals(name))
				return r;
		}
		return null;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Resource add(Resource resource) {
		resources.addElement(resource);
		return resource;
	}

	public Vector<ConstraintActivityResource> getResourceConstraints() {
		return resourceConstraints;
	}

	public ConstraintActivityResource add(
			ConstraintActivityResource resourceConstraint) {
		resourceConstraints.addElement(resourceConstraint);
		return resourceConstraint;
	}
	
	public boolean areAllActivitiesBound() {
		Vector<Activity> activities = getActivities();
		for (int i = 0; i < activities.size(); i++) {
			if (!activities.elementAt(i).getStart().isBound())
				return false;
		}
		return true;
	}

	public SearchStrategy strategyScheduleActivities() {
		Vector<Activity> activities = getActivities();
		Var[] vars = new Var[activities.size()];
		for (int i = 0; i < activities.size(); i++)
			vars[i] = activities.elementAt(i).getStart();
		Solver solver = getSolver();
		SearchStrategy strategy = solver.newSearchStrategy();
		strategy.setVars(vars);
		return strategy;
	}

	public SearchStrategy strategyAssignResources() {
		VectorVar vectorVars = new VectorVar();
		boolean costsDefined = false;
		for (int i = 0; i < getResources().size(); i++) {
			Resource resource = getResources().elementAt(i);
			if (resource.getCost() >= 0)
				costsDefined = true;
			for (int j = 0; j < resource.getActivityConstraints().size(); j++) {
				ConstraintActivityResource rc = (ConstraintActivityResource) resource
						.getActivityConstraints().elementAt(j);
				if (rc.getCapacity() == -1) {
					Var capacityVar = rc.getCapacityVar();
					vectorVars.add(capacityVar);
					capacityVar.setObject(resource);
				}
			}
//			for (int time = 0; time < getDuration(); time++) {
//				vectorVars.add(resource.getCapacityVar(time));
//			}
		}
		Var[] vars = vectorVars.toArray();

		Solver solver = getSolver();
		SearchStrategy strategy = solver.newSearchStrategy();
		strategy.setVars(vars);
		if (costsDefined)
			strategy.setVarSelector(new VarSelectorMinCost(strategy));

		return strategy;
	}
	
	public SearchStrategy strategyAssignResources(int time) {
		VectorVar vectorVars = new VectorVar();
		for (int i = 0; i < getResources().size(); i++) {
			Resource resource = getResources().elementAt(i);
			for (int j = 0; j < resource.getActivityConstraints().size(); j++) {
				ConstraintActivityResource rc = (ConstraintActivityResource) resource
						.getActivityConstraints().elementAt(j);
				Var start = rc.getActivity().getStart();
				if (start.isBound() && start.getValue() != time)
					continue;
				if (rc.getCapacity() == -1)
					vectorVars.add(rc.getCapacityVar());
			}
//			for (int time = 0; time < getDuration(); time++) {
//				vectorVars.add(resource.getCapacityVar(time));
//			}
		}
		Var[] vars = vectorVars.toArray();

		Solver solver = getSolver();
		SearchStrategy strategy = solver.newSearchStrategy();
		strategy.setVars(vars);

		return strategy;
	}
	
	/**
	 * Returns a vector of constrained integer variables which were
	 * added directly to this schedule using the Schedule's method add(Var)
	 * @return a vector of Var(s)
	 */
	public Vector<Var> getScheduleVars() {
		return scheduleVars;
	}
	
	public SearchStrategy strategyScheduleVars() {
		if (scheduleVars.isEmpty())
			return null;
		Var[] vars = new Var[scheduleVars.size()];
		for (int i = 0; i < scheduleVars.size(); i++)
			vars[i] = scheduleVars.elementAt(i);
		Solver solver = getSolver();
		SearchStrategy strategy = solver.newSearchStrategy();
		strategy.setVars(vars);
		return strategy;
	}

	public Solution assignResources() {
		javax.constraints.Solver solver = getSolver();
		solver.setSearchStrategy(strategyAssignResources());
		return solver.findSolution();
	}

	/**
	 * Schedules all activities.
	 * 
	 * @return Solution if a solution is found
	 * @return null if cannot schedule at least one activity
	 */
	public Solution scheduleActivities() {
		javax.constraints.Solver solver = getSolver();
		solver.setSearchStrategy(strategyScheduleActivities());
		Solution solution = solver.findSolution();
		if (solution == null)
			log("No solutions");
		else {
			log("SOLUTION:");
			//logActivities();
		}
		return solution;
	}

	public Solution scheduleActivitiesAndAssignResources() {
		Solver solver = getSolver();
		solver.setSearchStrategy(strategyScheduleActivities());
		solver.addSearchStrategy(strategyAssignResources());
		Solution solution = solver.findSolution();
		if (solution == null)
			log("No solutions");
		else {
			log("SOLUTION:");
			logActivities();
			logResources();
		}
		return solution;
	}

	/**
	 * Displays all activities
	 */
	public void logActivities() {
		for (int i = 0; i < activities.size(); i++) {
			Activity activity = (Activity) activities.elementAt(i);
			log(activity.toString());
		}
	}

	public Activity activity(String name, int from, int to) {
		Activity a = new BasicActivity(this, name, from, to);
		return add(a);
	}

	public Activity activity(String name, int duration) {
		Activity a = new BasicActivity(this, name, duration);
		return add(a);
	}

	/**
	 * Creates and returns an activity "name".
	 * 
	 * @param name
	 * @return an activity
	 */
	public Activity activity(String name) {
		return activity(name, getStart(), getEnd());
	}

	public Resource resource(String name, int capacityMax, ResourceType type) {
		Resource r = new BasicResource(this, name, capacityMax, type);
		return add(r);
	}

	public Resource resource(String name, int capacityMax) {
		Resource r = new BasicResource(this, name, capacityMax);
		return add(r);
	}

	public ResourceDisjunctive resourceDisjunctive(String name) {
		ResourceDisjunctive r = new BasicResourceBoolean(this, name);
		return (ResourceDisjunctive) add(r);
	}

	public ConstraintActivityResource addConstraintRequire(Activity activity,
			Resource resource, int capacity) {
		ConstraintActivityResource rc = new ConstraintRequire(activity,
				resource, capacity);
		activity.getResourceConstraints().add(rc);
		resource.getActivityConstraints().add(rc);
		add(rc);
		return rc;
	}

	/**
	 * Log all resources
	 */
	public void logResources() {
		for (int i = 0; i < resources.size(); i++) {
			Resource resource = (Resource) resources.elementAt(i);
			log(resource.toString());
		}
	}

	public void log(Solution solution) {
		if (solution == null) {
			log("Cannot log an empty solution");
			return;
		}
		log("SOLUTION #" + solution.getSolutionNumber());
		if (activities.size() > 0) {
			for (int i = 0; i < activities.size(); i++) {
				Activity act = activities.get(i);
				int start = solution.getValue(act.getStart().getName());
				String output = "\tActivity Name = " + act.getName() + " Start = " + start + 
						" Duration = " + act.getDuration();
				Vector<ConstraintActivityResource> rc = act.getResourceConstraints();
				if (rc.size() > 0) {
					for (int j = 0; j < rc.size(); j++) {
						ConstraintActivityResource c = (ConstraintActivityResource) rc.elementAt(j);
						String cap = null;
						if (c.getCapacityVar() == null) {
							cap = "[" + c.getCapacity() + "]";
						} else {
							int capValue = solution.getValue(c.getCapacityVar().getName());
							cap = "[" + capValue + "]";
						}
						if (cap.equals("[0]"))
							continue;
						output += " " + c.getType() + " " + c.getResource().getName() + cap;
					}
				}
				log(output);
			}
			if (!scheduleVars.isEmpty()) {
				for (int i = 0; i < scheduleVars.size(); i++) {
					String name = scheduleVars.get(i).getName();
					log(name + " = " + solution.getValue(name));
				}
			}
		}
	}
	
	public void save(Solution solution) {
		if (solution == null) {
			log("Cannot save an empty solution");
			return;
		}
		log("Saving SOLUTION #" + solution.getSolutionNumber());
		if (activities.size() > 0) {
			for (int i = 0; i < activities.size(); i++) {
				Activity act = activities.get(i);
				int start = solution.getValue(act.getStart().getName());
				post(act.getStart(),"=",start);
				Vector<ConstraintActivityResource> rc = act.getResourceConstraints();
				if (rc.size() > 0) {
					for (int j = 0; j < rc.size(); j++) {
						ConstraintActivityResource c = (ConstraintActivityResource) rc.elementAt(j);
						String cap = null;
						if (c.getCapacityVar() != null) {
							int capValue = solution.getValue(c.getCapacityVar().getName());
							post(c.getCapacityVar(),"=",capValue);
						}
					}
				}
			}
			if (!scheduleVars.isEmpty()) {
				for (int i = 0; i < scheduleVars.size(); i++) {
					Var var = scheduleVars.get(i);
					String name = var.getName();
					post(var,"=",solution.getValue(name));
				}
			}
		}
	}

	public ConstraintActivityResource addConstraintRequire(Activity activity,
			Resource resource, Var capacityVar) {
		ConstraintActivityResource rc = new ConstraintRequire(activity,
				resource, capacityVar);
		activity.getResourceConstraints().add(rc);
		resource.getActivityConstraints().add(rc);
		add(rc);
		return rc;
	}

	// public ConstraintActivityResource addConstraintConsume(Activity activity,
	// Resource resource, int capacity) {
	// ConstraintActivityResource rc = new ConstraintConsume(activity,
	// resource, capacity);
	// activity.getResourceConstraints().add(rc);
	// resource.getActivityConstraints().add(rc);
	// add(rc);
	// return rc;
	// }
	//
	// public ConstraintActivityResource addConstraintConsume(Activity activity,
	// Resource resource, Var capacityVar) {
	// ConstraintActivityResource rc = new ConstraintConsume(activity,
	// resource, capacityVar);
	// activity.getResourceConstraints().add(rc);
	// resource.getActivityConstraints().add(rc);
	// add(rc);
	// return rc;
	// }

	// Precedence constraints
	public Constraint post(Activity act1, String oper, Activity act2) {
		Constraint c = act1.start(oper, act2, 0);
		post(c);
		return c;
	}

	public Constraint post(Activity act1, String oper, Activity act2, int offset) {
		// Var var1 = act1.getStart();
		// Var var2 = act2.getStart();
		// if (oper.equals(">")) {
		// var1 = act1.getStart();
		// var2 = act2.getEnd().minus(1);
		// } else if (oper.equals(">=")) {
		// var1 = act1.getStart();
		// var2 = act2.getStart();
		// } else if (oper.equals("<=")) {
		// var1 = act1.getStart();
		// var2 = act2.getStart();
		// } else if (oper.equals("<")) {
		// var1 = act1.getEnd().minus(1);
		// var2 = act2.getStart();
		// } else if (oper.equals("=")) {
		// var1 = act1.getStart();
		// var2 = act2.getStart();
		// }
		//
		// Var var2off = var2;
		// if (offset != 0)
		// var2off = var2.plus(offset);
		//
		// return post(var1, oper, var2off);
		Constraint c = act1.start(oper, act2, offset);
		post(c);
		return c;
	}

	public void post(Activity act, String oper, int time) { 
		// Var var = act.getStart();
		// return post(var, oper, time);
		Constraint c = act.start(oper, time);
		post(c);
	}
	
	/**
	 * Creates and posts a constraint that all activities in the array "activities)
	 * do not intersect
	 * @param activities
	 * @return a posted constraint
	 */
	public void postAllDiff(Activity[] activities) {
		for (int i = 0; i < activities.length-1; i++) {
			for (int j = i+1; j < activities.length; j++) {
				//log(activities[i].getName() + " != " + activities[j].getName());
				post(activities[i].diff(activities[j]));
			}
		}
	}

//	/**
//	 * Saves the solution inside activities and resources
//	 * 
//	 * @param solution
//	 */
//	public void save(Solution solution) {
//		for (int i = 0; i < activities.size(); i++) {
//			Activity activity = (Activity) activities.elementAt(i);
//			activity.save(solution);
//		}
//		for (int i = 0; i < resources.size(); i++) {
//			Resource resource = (Resource) resources.elementAt(i);
//			resource.save(solution);
//		}
//	}
	
	public Var[] getConstraintCapacites(Resource resource) {
		Vector<Constraint> constraints = resource.getActivityConstraints();
		if (constraints.size() == 0)
			return null;
		Var[] capacities = new Var[constraints.size()];
		for (int i = 0; i < capacities.length; i++) {
			ConstraintActivityResource ci = (ConstraintActivityResource)constraints.get(i);
			Var var = ci.getCapacityVar();
			if (var == null) { 
				 var = variable(ci.getCapacity(),ci.getCapacity());
			}
			capacities[i] = var;
		}
		return capacities;
	}
	
	public Var[] getResourceOccupancies(Resource resource) {
        return getConstraintCapacites(resource);
    }
	
	public Var[] getResourceOccupancies() {
	    Var[] occupanciesOfAllResources = new Var[resources.size()];
	    for (int i = 0; i < resources.size(); i++) {
            Resource r = (Resource) resources.elementAt(i);
            Var[] occupanciesOfOneResource = getResourceOccupancies(r);
            if (occupanciesOfOneResource != null) {
                occupanciesOfAllResources[i] = sum(occupanciesOfOneResource);
            }
            else {
                occupanciesOfAllResources[i] = variable(0,0);
            }
        }
	    return occupanciesOfAllResources;
	}
	
	/**
	 * Adds a Var variable "var" to the problem, and returns the newly added Var.
	 *
	 * @param var the variable to add to the problem.
	 * @return the Var variable added to the problem.
	 */
	public Var add(Var var) {
		super.add(var);
		scheduleVars.add(var);
		return var;
	}

}
