//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

import java.util.Vector;

import javax.constraints.Constraint;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.ConstraintActivityResource;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.ResourceDisjunctive;
import javax.constraints.scheduler.ResourceDisjunctivePool;
import javax.constraints.scheduler.ResourcePool;
import javax.constraints.scheduler.ResourceType;
import javax.constraints.scheduler.Schedule;

/**
 * This class represents constrained scheduling activities.
 * 
 */

public class BasicActivity extends SchedulingObject implements Activity {

	private final Var start;
	
	private int solutionStart;

	private final Var end;
	
	private int solutionEnd;

	private final Var durationVar;

	private final int duration;

	private Object object;

	private Vector<ConstraintActivityResource> resourceConstraints;
	
	String startName() {
		return name.trim()+".start";
	}
	String endName() {
		return name.trim()+".end";
	}
	String durationName() {
		return name.trim()+".duration";
	}

	public BasicActivity(ScheduleImpl problem, String name, int from, int to) {
		super(problem);
		setName(name);
		if (from < problem.getStart() || from >= to || to > problem.getEnd())
			throw new RuntimeException("Activity " + name + "[" + from + ":" + to
					+ ") is invalid");
		start = problem.variable(startName(), from, to);
		end = problem.variable(endName(), from, to);
		duration = to - from;
		durationVar = problem.variable(durationName(), 0, to - from + 1);
		object = null;
		initialize();
	}

	public BasicActivity(ScheduleImpl problem, String name, int duration) {
		super(problem);
		setName(name);
		if (duration > problem.getDuration())
			throw new RuntimeException("Activity " + name + " duration=" + duration
					+ " is larger than " + problem.getDuration());
		start = problem.variable(startName(), problem.getStart(), problem.getEnd());
		end = problem.variable(endName(), problem.getStart(), problem.getEnd());
		this.duration = duration;
		durationVar = problem.variable(durationName(), duration, duration);
		object = null;
		initialize();
	}

	protected void initialize() {
		solutionStart = -1; // not defined
		solutionEnd = -1; // not defined
		getSchedule().post(end, "=", start.plus(duration));
		//getSchedule().post(new Var[] {end,start.negative()},"=", duration);// removed plus to be used with a linear solver
		resourceConstraints = new Vector<ConstraintActivityResource>();
	}

	public int getDuration() {
		return duration;
	}

	public Var getDurationVar() {
		return durationVar;
	}

	public Var getEnd() {
		return end;
	}

	public int getEndMax() {
		return end.getMax();
	}

	public Var getStart() {
		return start;
	}

	public int getStartMin() {
		return start.getMin();
	}

	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}

	// Precedence constraints
	public Constraint start(String oper, Activity act2) {
		return start(oper, act2, 0);
	}

	public Constraint start(String oper, Activity act2, int offset) {
		Schedule s = getSchedule();
		Var var1 = this.getStart();
		Var var2 = act2.getStart();
		if (oper.equals(">")) {
			var1 = this.getStart();
			var2 = act2.getEnd().minus(1);
		} else if (oper.equals(">=")) {
			var1 = this.getStart();
			var2 = act2.getStart();
		} else if (oper.equals("<=")) {
			var1 = this.getStart();
			var2 = act2.getStart();
		} else if (oper.equals("<")) {
			var1 = this.getEnd().minus(1);
			var2 = act2.getStart();
		} else if (oper.equals("=")) {
			var1 = this.getStart();
			var2 = act2.getStart();
		}
		
//		if (offset == 0)
//			return s.linear(var1, oper, var2);
//		
//		return s.post(new Var[] { var1, var2.negative() }, oper, offset); // removed "plus" to be used with a linear solver

		Var var2off = var2;
		if (offset != 0)
			var2off = var2.plus(offset);
		
		return s.linear(var1, oper, var2off);
		
		
	}
	
	public Constraint diff(Activity act2) {
		
		Constraint c1 = this.start(">",act2);
		Constraint c2 = act2.start(">",this);
		return c1.or(c2);
	}
	
	public Constraint start(String oper, int time) {
		Var var = this.getStart();
		return getSchedule().linear(var, oper, time);
	}

	public Vector<ConstraintActivityResource> getResourceConstraints() {
		return resourceConstraints;
	}

	public ConstraintActivityResource requires(Resource resource, int capacity) {
		// ConstraintActivityResource c =
		// getSchedule().addConstraintRequire(this, resource,
		// capacity);
		ConstraintActivityResource rc;
		if (resource.getType().equals(ResourceType.CONSUMABLE)) {
		    rc = new ConstraintConsume(this, resource,capacity);
		}
		else {
		    rc = new ConstraintRequire(this, resource,capacity);
		}
		this.getResourceConstraints().add(rc);
		resource.getActivityConstraints().add(rc);
		getSchedule().add(rc);
		rc.post();
		return rc;
	}

	public ConstraintActivityResource requires(Resource resource,
			Var capacityVar) {
		// ConstraintActivityResource c =
		// getSchedule().addConstraintRequire(this, resource,
		// capacityVar);
		ConstraintActivityResource rc; 
		if (resource.getType().equals(ResourceType.CONSUMABLE)) {
            rc = new ConstraintConsume(this,resource,capacityVar);
        }
		else {
            rc = new ConstraintRequire(this,resource,capacityVar);
        }
		this.getResourceConstraints().add(rc);
		resource.getActivityConstraints().add(rc);
		getSchedule().add(rc);
		rc.post();
		return rc;
	}

	public ConstraintActivityResource requires(ResourceDisjunctive resource) {
		return requires(resource, 1);
	}

	public Constraint requires(ResourceDisjunctive[] resources) {
		if (resources.length == 1)
			return requires(resources[0]);
		Schedule schedule = getSchedule();
		Var[] capacityVars = new Var[resources.length];
		for (int i = 0; i < capacityVars.length; i++) {
			capacityVars[i] = schedule.variable(
					getName().trim() + " requires " + resources[i].getName().trim(), 0, 1);
			requires(resources[i], capacityVars[i]);
		}
		Var sum = schedule.sum(capacityVars);
		return getSchedule().post(sum,"=",1);
	}

	public Constraint requires(ResourceDisjunctive resource1,
			ResourceDisjunctive resource2) {
		ResourceDisjunctive[] resources = { resource1, resource2 };
		return requires(resources);
	}

	public Constraint requires(ResourceDisjunctive resource1,
			ResourceDisjunctive resource2, ResourceDisjunctive resource3) {
		ResourceDisjunctive[] resources = { resource1, resource2, resource3 };
		return requires(resources);
	}

	/**
	 * This method posts a constraint that states: this activity requires one
	 * and only one resource from the pool of alternative disjunctive resources.
	 * 
	 * @param pool
	 *            of disjunctive resources
	 * @return the posted constraint
	 */
	public Constraint requires(ResourceDisjunctivePool pool) {
		BasicResourceDisjunctivePool poolI = (BasicResourceDisjunctivePool) pool;
		ResourceDisjunctive[] array = (ResourceDisjunctive[]) poolI.toArray();
		return requires(array);
	}

	/**
	 * This method posts a constraint that states: this activity requires one
	 * and only one resource from the pool of alternative disjunctive resources.
	 * There is an assumption that all resources within the pool are ordered
	 * starting from 0. The parameter-variable indexVar serves as an index
	 * within the "pool" that points to the actually assigned resource.
	 * 
	 * @param pool
	 *            of disjunctive resources
	 * @param indexVar
	 *            a constrained variable
	 * @return the posted constraint
	 */
	public Constraint requires(ResourceDisjunctivePool pool, Var indexVar) {
		BasicResourceDisjunctivePool poolI = (BasicResourceDisjunctivePool) pool;
		if (poolI.size() == 1)
			return requires(poolI.get(0));
		ResourceDisjunctive[] array = (ResourceDisjunctive[]) poolI.toArray();
		Var[] capacityVars = new Var[array.length];
		for (int i = 0; i < capacityVars.length; i++) {
			capacityVars[i] = getSchedule().variableBool(
					getName() + " requires " + array[i].getName());
			requires(array[i], capacityVars[i]);
		}
		getSchedule().postElement(capacityVars, indexVar, "=", 1);
		return getSchedule().post(capacityVars, "=", 1);

	}

	/**
	 * This method posts a constraint that states: this activity requires a
	 * certain integer "capacity" from the pool of alternative resources.
	 * 
	 * @param pool
	 *            of resources
	 * @param capacity
	 *            int
	 * @return the posted constraint
	 */
	public Constraint requires(ResourcePool pool, int capacity) {
		getSchedule()
				.log("Constraint requires(ResourcePool pool, int capacity) not implemented yet");
		return null;
	}

	/**
	 * This method posts a constraint that states: this activity requires
	 * certain variable "capacityVar" from the pool of alternative resources.
	 * 
	 * @param pool
	 *            of resources
	 * @param capacityVar
	 *            Var
	 * @return the posted constraint
	 */
	public Constraint requires(ResourcePool pool, Var capacityVar) {
		getSchedule()
				.log("Constraint requires(ResourcePool pool, Var capacityVar) not implemented yet");
		return null;
	}
	
	public void doNotShareResourcesWith(Activity act) {
		Vector<ConstraintActivityResource> rc1 = getResourceConstraints();
		Vector<ConstraintActivityResource> rc2 = act.getResourceConstraints();
		Schedule s = getSchedule();
		if (rc1.size() == 0 || rc2.size()== 0) 
			return;
		for (int i = 0; i < rc1.size(); i++) {
			ConstraintActivityResource ci = (ConstraintActivityResource) rc1.elementAt(i);
			if (ci.getResource() == null)
				continue;
			Var capi = ci.getCapacityVar();
			if (capi == null)
				continue;
			for (int j = 0; j < rc2.size(); j++) {
				ConstraintActivityResource cj = (ConstraintActivityResource) rc2.elementAt(j);
				if (cj.getResource() == null)
					continue;
				if (ci.getResource().getName().equals(cj.getResource().getName())) {
					Var capj = cj.getCapacityVar();
					if (capj == null)
						continue;
					s.post(capi.plus(capj),"<=",1); // removed plus to be used with a linear solver
					//s.post(new Var[] { capi, capj }, "<=", 1);
				}
			}
		}
	}

	public boolean isBound() {
		return start.isBound() && end.isBound();
	}

	/**
	 * @return a String representation of this activity in a form of
	 *         "name [startMin..startMax -- duration --> endMin..endMax)". For
	 *         example, "carpentry[7 -- 2 --> 9)" means that job "carpentry"
	 *         starts at day 7, lasts 2 days and ends on day 9 (not including
	 *         9).
	 * 
	 *         "carpentry[7 -- 2 --> 9) requires resource Joe[2]" additionally
	 *         means that this activity requires a resource with capacity 2.
	 * 
	 *         "masonry[0..12 -- 7 --> 7..19)" means that activity "masonry"
	 *         starts at sometime between 0 and 12, lasts 7 units of time, and
	 *         ends sometime between 7 and 19 (not including 19).
	 * 
	 */
	public String toString() {
		int startMin = getStart().getMin();
		int startMax = getStart().getMax();
		int endMin = getEnd().getMin();
		int endMax = getEnd().getMax();
		StringBuffer buf = new StringBuffer();
		String start;
		if (startMin == startMax)
			start = "" + startMin;
		else {
			if (getSolutionStart() > -1)
				start = "" + getSolutionStart();
			else 
				start = startMin + ".." + startMax;
		}
		String end;
		if (endMin == endMax)
			end = "" + endMin;
		else {
			if (getSolutionEnd() > -1)
				end = "" + getSolutionEnd();
			else 
				end = endMin + ".." + endMax;
		}
		buf.append(getName() + "[" + start + " -- "
				+ getDuration() + " --> " + end + ")");
		/*
		Vector<ConstraintActivityResource> rc = getResourceConstraints();
		if (rc.size() > 0) {
			for (int i = 0; i < rc.size(); i++) {
				ConstraintActivityResource c = 
				        (ConstraintActivityResource) rc.elementAt(i);
				if (!c.getActivity().getName().equals(getName())) // added on Aug 15, 2024
				    continue; 
				String cap = null;
				if (c.getCapacityVar() == null) {
					cap = "[" + c.getCapacity() + "]";
				} else {
					int capMin = c.getCapacityVar().getMin();
					int capMax = c.getCapacityVar().getMax();
					cap = "[" + capMin
							+ (capMin == capMax ? "" : ".." + capMax)
							+ "]";
				}
				if (cap.endsWith("[0]"))
					continue;
				// if (i > 0)
				// buf.append(",");
				buf.append(" " + c.getType() + " ");
				buf.append(c.getResource().getName());
				buf.append(cap);
				buf.append(" ");
			}
			buf.append(" in a time");
		}
		*/
		return buf.toString();
	}
	
	/**
	 * 
	 * @return a calculated start of this activity 
	 */
	public int getSolutionStart() {
		return solutionStart;
	}
	
	/**
	 * 
	 * Sets a calculated start of this activity 
	 */
	public void setSolutionStart(int start) {
		solutionStart = start;
	}
	
	/**
	 * 
	 * @return a calculated end of this activity 
	 */
	public int getSolutionEnd() {
		return solutionEnd;
	}
	
	/**
	 * 
	 * Sets a calculated end of this activity 
	 */
	public void setSolutionEnd(int end) {
		solutionEnd = end;
	}
	
//	public void save(Solution solution) {
//		int s = solution.getValue(startName());
//		setSolutionStart(s);
//		int e = s + duration;
//		setSolutionEnd(e);
//	}

}
