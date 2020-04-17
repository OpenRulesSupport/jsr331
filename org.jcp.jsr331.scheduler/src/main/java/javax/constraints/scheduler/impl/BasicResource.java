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
import javax.constraints.scheduler.ConstraintActivityResource;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.ResourceType;
import javax.constraints.scheduler.Schedule;

/**
 * This class represents constrained scheduling resources
 *
 */

public class BasicResource extends SchedulingObject implements Resource {
	
	ResourceType type;

	private final int timeMin; // availability start time

	private final int timeMax; // availability end time (not including)
	
	private int cost; 

	private final Var[] capacities; // capacities for each time between timeMin
									// and timeMax
	
	private final Vector<Constraint> activityConstraints;

	public BasicResource(Schedule schedule, String name, int timeMin, int timeMax, int capacityMax,
			         ResourceType type) {
		super(schedule);
		setType(type);
		this.timeMax = timeMax;
		this.timeMin = timeMin;
		if (timeMin >= timeMax)
			throw new RuntimeException("Resourse " + name + ": invalid [timeMin;timemax)");
		cost = -1;
		capacities = new Var[getDuration()];
		for (int t = timeMin; t < timeMax; t++) {
			capacities[t] = schedule.createVariable(name+".t" + t, 0, capacityMax);
			//problem.remove("t"+t);
		}
		// for consumable resources sum of all daily capacities 
		if (type.equals(ResourceType.CONSUMABLE)) {
			schedule.post(capacities,"=",capacityMax);
		}
		setName(name);
		activityConstraints = new Vector<Constraint>();
	}
	
	public BasicResource(Schedule schedule, String name, int timeMin, int timeMax, int capacityMax) {
		this(schedule,name,timeMin,timeMax,capacityMax,ResourceType.DISCRETE);
	}

	public BasicResource(Schedule schedule, String name, int capacityMax) {
		this(schedule, name,schedule.getStart(), schedule.getEnd(), capacityMax);
	}
	
	public BasicResource(Schedule schedule, String name, int capacityMax, ResourceType type) {
		this(schedule, name, schedule.getStart(), schedule.getEnd(), capacityMax, type);
	}
	

	public ResourceType getType() {
		return type;
	}

	public void setType(ResourceType type) {
		this.type = type;
	}

	public Var[] getCapacities() {
		return capacities;
	}

	/**
	 * Sets resource maximum capacity at the specified moment of time.
	 *
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception
	 */
	public void setCapacityMax(int time, int capacity) {
		Constraint c = capacityMaxConstraint(time,capacity);
		try {
			getSchedule().post(c);
		} catch (Exception e) {
			throw new RuntimeException("cannot post capacityMaxConstraint(" + 
		                 time + "," + capacity + ")");
		}
	}
	
	/**
	 * Creates and returns without posting a constraint that when posted
	 * sets resource maximum capacity at the specified moment of time.
	 *
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception
	 */
	public Constraint capacityMaxConstraint(int time, int capacity) {
		int t = time - timeMin;
		if (t < 0 || t > timeMax)
			throw new RuntimeException("capacityMaxConstraint: time is out of bounds");
		return getSchedule().linear(capacities[t],"<=",capacity);
	}

	/**
	 * Sets resource minimum capacity at the specified moment of time.
	 *
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception
	 */
	public void setCapacityMin(int time, int capacity) {
		Constraint c = capacityMinConstraint(time,capacity);
		try {
			getSchedule().post(c);
		} catch (Exception e) {
			throw new RuntimeException("cannot post capacityMinConstraint(" + 
		                 time + "," + capacity + ")");
		}
	}
	
	/**
	 * Creates and returns without posting a constraint that when posted
	 * sets resource minimal capacity at the specified moment of time.
	 *
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception
	 */
	public Constraint capacityMinConstraint(int time, int capacity) {
		int t = time - timeMin;
		if (t < 0 || t > timeMax)
			throw new RuntimeException("capacityMinConstraint: time is out of bounds");
		return getSchedule().linear(capacities[t],">=",capacity);
	}

	/**
	 * Sets resource maximum capacity at the specified interval of time
	 * [time1;time2)
	 *
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws Failure
	 */
	public void setCapacityMax(int time1, int time2, int capacity) {
		Constraint[] constraints = capacityMaxConstraints(time1, time2, capacity);
		for (int i = 0; i<constraints.length; i++) {
			try {
				getSchedule().post(constraints[i]);
			} catch (Exception e) {
				throw new RuntimeException("cannot post capacityMaxConstraints("
						+ time1 + " +," + time2 + "," + capacity + ")");
			}
		}
	}
	
	/**
	 * Creates and returns an array of constraints that when all posted 
	 * set resource maximal capacity at the specified interval of time
	 * [time1;time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws an exception when time2 < time1
	 */
	public Constraint[] capacityMaxConstraints(int time1, int time2, int capacity) {
		int t1 = time1 - timeMin;
		int t2 = time2 - timeMin;
		if (t1 < 0 || t1 >= t2 || t2 > timeMax)
			throw new RuntimeException("capacityMaxConstraints: times are out of bounds");
		Constraint[] constraints = new Constraint[t2-t1];
		int n = 0;
		for (int t = t1; t < t2; t++) {
			constraints[n++] = getSchedule().linear(capacities[t],"<=",capacity);
		}
		return constraints;
	}

	/**
	 * Sets resource minimum capacity at the specified interval of time [time1;
	 * time2)
	 *
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws Failure
	 */
	public void setCapacityMin(int time1, int time2, int capacity) {
		Constraint[] constraints = capacityMinConstraints(time1, time2, capacity);
		for (int i = 0; i<constraints.length; i++) {
			try {
				getSchedule().post(constraints[i]);
			} catch (Exception e) {
				throw new RuntimeException("cannot post capacityMinConstraints("
						+ time1 + " +," + time2 + "," + capacity + ")");
			}
		}
	}
	
	/**
	 * Creates and returns an array of constraints that when all posted 
	 * set resource maximal capacity at the specified interval of time
	 * [time1;time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws an exception when time2 < time1
	 */
	public Constraint[] capacityMinConstraints(int time1, int time2, int capacity) {
		int t1 = time1 - timeMin;
		int t2 = time2 - timeMin;
		if (t1 < 0 || t1 >= t2 || t2 > timeMax)
			throw new RuntimeException("capacityMinConstraints: times are out of bounds");
		Constraint[] constraints = new Constraint[t2-t1];
		int n = 0;
		for (int t = t1; t < t2; t++) {
			constraints[n++] = getSchedule().linear(capacities[t],">=",capacity);
		}
		return constraints;
	}

	/**
	 * Returns resource maximum capacity at the specified moment of time
	 *
	 * @param time
	 *            Time for capacity query
	 * @throws Failure
	 * @return Capacity
	 */
	public int getCapacityMax(int time) {
		int t = time - timeMin;
		if (t < 0 || t > timeMax)
			throw new RuntimeException("getCapacityMax: time is out of bounds");
		return capacities[t].getMax();
	}

	/**
	 * Returns resource minimum capacity at the specified moment of time
	 *
	 * @param time
	 *            Time for capacity query
	 * @throws Failure
	 * @return Capacity
	 */
	public int getCapacityMin(int time) {
		int t = time - timeMin;
		if (t < 0 || t > timeMax)
			throw new RuntimeException("getCapacityMin: time is out of bounds");
		return capacities[t].getMin();
	}

	/**
	 * @return Resource availability durarion
	 */
	public int getDuration() {
		return timeMax - timeMin;
	}

	/**
	 * Returns internal Var variable associated with capacity at specified
	 * moment of time
	 *
	 * @param time
	 *            Moment of time
	 * @return Capacity variable
	 * @throws Exception
	 */
	public Var getCapacityVar(int time) {
		int t = time - timeMin;
		if (t < 0 || t > timeMax)
			throw new RuntimeException("setCapacityVar: time is out of bounds");
		return capacities[t];
	}

	/**
	 * Returns resource availability start time
	 *
	 * @return Resource availability start time
	 */
	public int getTimeMin() {
		return timeMin;
	}

	/**
	 * Returns resource availability end time
	 *
	 * @return Resource availability end time
	 */
	public int getTimeMax() {
		return timeMax;
	}

	public Vector<Constraint> getActivityConstraints() {
		return activityConstraints;
	}

	public void add(ConstraintActivityResource rc) {
		activityConstraints.addElement(rc);
	}
	
	public Var setCost(int x1, int x2, int y1, int y2, int cost) {
		getSchedule().log("Resource method Var setCost(int x1, int x2, int y1, int y2, int cost) is not implemented yet");
		return null;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
	public int getCost() {
		return cost;
	}

	public boolean isBound() {
		for (int t = 0; t < getCapacities().length; t++) {
			if (!getCapacities()[t].isBound())
				return false;
		}
		return true;
	}
	
//	public void save(Solution solution) {
//			for (int i = 0; i < getActivityConstraints().size(); i++) {
//				ConstraintActivityResource rc = 
//					(ConstraintActivityResource)getActivityConstraints().elementAt(i);
//				if (rc.getCapacity() == -1) {
//					Var var = rc.getCapacityVar();
//					int value = solution.getValue(var.getName());
//					var.getProblem().post(var,"=",value);
//				}
//			}
//			for (int time = 0; time < getDuration(); time++) {
//				Var var = getCapacityVar(time);
//				int value = solution.getValue(var.getName());
//				var.getProblem().post(var,"=",value);
//			}
//	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Resource "+getName() + ":");
		int n = 0;
		for(int i=timeMin; i < timeMax; i++) {
			Var capVar = capacities[i];
			if (!capVar.isBound() || capVar.getValue() > 0) {
				buf.append(" "+capacities[i].toString());
				if (n>0 && n%10 == 0)
					buf.append("\n");
				n++;
			}
		}
		return buf.toString();
	}

}
