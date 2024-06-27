package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/

/**
 * javax.constraints is a Java package for modeling and solving different constraint
 * satisfaction problems. javax.constraints represents two interfaces:
 * <ol>
 * <li> generic business interface for incorporation of a constraint satisfaction
 * problem (CSP) into different tools and business applications.
 * <li> generic technical interface for different implementations of a constraint
 * solver.
 * </ol>
 *
 * BizProblem - a placeholder for all components that represent a
 * constraint satisfaction problem (CSP). A CSP usually consists of
 * two parts:
 * <ol>
 * <li> Problem Definition: contains all constrained objects and
 * constraints that define and control relationships between constrained objects
 * <li> Problem Resolution: contains search algorithm (goals) that allows to solve
 * the problem
 * </ol>
 *
 * The decision variables are represented in form of Java objects
 * which may use the predefined constrained variables types such as Var, CorkReal,
 * CorkSet.
 *
 * The constraints themselves are objects inherited from a generic class CorkConstraint.
 * Cork Solver support major binary and global constraints required for the practical CP
 * programming. It is possible to define new constraints.
 *
 * <p>
 * To find the problem solutions, Cork Solver uses search algorithms presented
 * using objects of the predefined type CorkGoal. Goals are building blocks to define
 * different search algorithms.
 *
 * @see Var
 * @see Constraint
 * @see Goal
 *
 * @author JF
*/
import java.util.Vector;

import javax.constraints.Problem;
import javax.constraints.Constraint;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.Schedule;


public class BizProblem implements java.io.Serializable {

	static final long serialVersionUID = 214525463;

	private Problem	csp;

	private Schedule schedule;

	String id; // also used as a filename <id>.html for problem

	private String name;

	private int solutionNumber; // from 0 to maxNumberOfSolutions

	private int numberOfFoundSolutions;

	private int maxNumberOfSolutions; // -1 mean unlimited

	private final Vector integers;

	private final Vector constraints;

	private final Vector activities;

	private BizObject objective;

	private boolean findSolutionVisible;
	private boolean minimizeVisible;
	private boolean maximizeVisible;
	private boolean scheduleActivitiesVisible;

	private int lastAction;

	public BizProblem() {
		this("BizProblem");
	}

	public BizProblem(String id) {
		this(id,id);
	}

	public BizProblem(String id, String name) {
		this.id = id;
		this.name = name;
		integers = new Vector(10, 5);
		constraints = new Vector(5, 2);
		objective = null;
		solutionNumber = 0;
		maxNumberOfSolutions = -1; // unlimited
		numberOfFoundSolutions = 0;
		findSolutionVisible = true;
		minimizeVisible = false;
		maximizeVisible = false;
		scheduleActivitiesVisible = false;
		lastAction = BizConstant.ACTION_UNKNOWN;
		csp = null;
		activities = new Vector();
	}

	public BizProblem(Problem csp) {
		this(csp.getName());
		this.csp = csp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Problem getCsp() {
		return csp;
	}

	public void setCsp(Problem csp) {
		this.csp = csp;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return true if there are no constrained objects defined
	 */
	public boolean isEmpty() {
		return integers.size() == 0;
	}

	/**
	 * @return the number of the last found solution or 0 if no solution
	 * have been found (or the search for a solution was not launched yet)
	 */
	public int getSolutionNumber() {
		return solutionNumber;
	}

	public void setSolutionNumber(int solutionNumber) {
		this.solutionNumber = solutionNumber;
	}

	/**
	 * @return the maximal number of solutions any search can look for.
	 * By default it returns -1 that means there are no limitations for
	 * a maximal number of solutions
	 */
	public int getMaxNumberOfSolutions() {
		return maxNumberOfSolutions;
	}

	/**
	 * Sets a limit for the maximal number of solutions any search can look for.
	 * -1 means there are no limitations for a maximal number of solutions
	 */
	public void setMaxNumberOfSolutions(int maxNumberOfSolutions) {
		this.maxNumberOfSolutions = maxNumberOfSolutions;
	}

	public int getNumberOfFoundSolutions() {
		return numberOfFoundSolutions;
	}

	public void setNumberOfFoundSolutions(int numberOfFoundSolutions) {
		this.numberOfFoundSolutions = numberOfFoundSolutions;
	}

	/**
	 * Adds a Var variable "var" to the problem
	 * @param var
	 * @return BizInt
	 */
	public BizInt add(BizInt var) {
		integers.addElement(var);
		return var;
	}

	/**
	 * Creates a Var variable based on the symbolicExpression.
	 * Adds this variable to the problem.
	 * @param symbolicExpression a string that defines a constrained expression that uses already
	 * defined variables.
	 * @return a Var variable or null if there is an error inside the symbolicExpression or
	 * if this method is not defined by a concrete solver implementation
	 */
	public BizInt add(String symbolicExpression) {
		log("The implementation does not support symbolic Var expressions like:\n"+
				symbolicExpression);
		return null;
	}

	/**
	 * Creates a Var with the name "name", and domain [min;max] and
	 * adds this variable to the problem.
	 * @param name a string
	 * @param min int
	 * @param max int
	 * @return
	 */
	public BizInt addVar(String name, int min, int max) {
		BizInt var = new BizInt(this,name,min,max);
		add(var);
		return var;
	}

	public BizInt addInt(String name, int min, int max) {
		return addVar(name,min,max);
	}

	/**
	 * Creates a BizInt using a Var
	 * @return
	 */
	public BizInt addVar(Var ci) {
		BizInt bi = new BizInt(this,ci);
		bi.setName(ci.getName());
		return add(bi);
	}

	public BizInt addInt(Var ci) {
		return addVar(ci);
	}

	public BizInt[] getIntegers() {
		BizInt[] array = new BizInt[integers.size()];
		for (int i = 0; i < integers.size(); i++) {
			array[i] = (BizInt) integers.elementAt(i);
		}
		return array;
		// return integers.toArray();
	}

	public BizInt getInt(String name) {
		for(int i=0; i<integers.size(); i++) {
			BizInt var = (BizInt)integers.elementAt(i);
			if (var.getName().equals(name))
				return var;
		}
		log("Cannot find Var "+name);
		return null;
	}

	public Var var(String name) {
		BizInt var = getInt(name);
		if (var != null)
			return var.getVar();
		return null;
	}

	public Vector getActivities() {
		return activities;
	}

	public BizActivity addActivity(String name, int duration) {
		BizActivity act = new BizActivity(this,name,duration);
		add(act);
		return act;
	}

	public void add(BizActivity act) {
		activities.add(act);
	}

	public BizActivity getActivity(String name) {
		for(int i=0; i<activities.size(); i++) {
			BizActivity act = (BizActivity)activities.elementAt(i);
			if (act.getName().equals(name))
				return act;
		}
		log("Cannot find Activity "+name);
		return null;
	}

	public Activity activity(String name) {
		BizActivity act = getActivity(name);
		if (act != null)
			return act.getActivity();
		return null;
	}

	public Resource resource(String name) {
		if (getSchedule() == null)
			return null;
		return getSchedule().getResource(name);
	}

	/**
	 * Adds all integers from the underlying CSP
	 */
	public void addIntegers() {
		Var[] vars = csp.getVars();
		for(int i=0; i < vars.length; i++)
			addInt(vars[i]);
	}

	/**
	 * Adds all constraints from the underlying CSP
	 */
	public void addConstraints() {
		Object[] constraints = csp.getConstraints();
		for(int i=0; i < constraints.length; i++)
			addConstraint((Constraint)constraints[i]);
	}


	/**
	 * Creates a BizConstraint with the name "name" using a CorkConstraint
	 * @param cc a CorkConstraint
	 * @param name a string
	 * @param min int
	 * @param max int
	 * @return BizConstraint
	 */
	public BizConstraint addConstraint(Constraint cc) {
		BizConstraint bc = new BizConstraint(this,cc,cc.getName());
		return add(bc);
	}

	public BizConstraint add(BizConstraint constraint) {
		constraints.addElement(constraint);
		return constraint;
	}

	public BizConstraint[] getConstraints() {
		BizConstraint[] array = new BizConstraint[constraints.size()];
		for (int i = 0; i < constraints.size(); i++) {
			array[i] = (BizConstraint) constraints.elementAt(i);
		}
		return array;
		//return constraints.toArray();
	}

	public void resetConstraints() {
		for (int i = 0; i < constraints.size(); i++) {
			BizConstraint ct = (BizConstraint) constraints.elementAt(i);
			ct.resetStatus();
		}
	}

	/**
	 * @return true if lastAction is ACTION_SOLVE and getSolutionNumber() > 0
	 */
	public boolean isSolved() {
		if (lastAction == BizConstant.ACTION_UNKNOWN)
			return false;
		return lastAction == BizConstant.ACTION_SOLVE && getNumberOfFoundSolutions() > 0;
	}

	/**
	 * @return true if the method Minimize was applied and solution
	 * was found
	 */
	public boolean isMinimized() {
		if (lastAction == BizConstant.ACTION_UNKNOWN)
			return false;
		return lastAction == BizConstant.ACTION_MINIMIZE && getNumberOfFoundSolutions() > 0;
	}

	/**
	 * @return true if the method Maximize was applied and solution
	 * was found
	 */
	public boolean isMaximized() {
		if (lastAction == BizConstant.ACTION_UNKNOWN)
			return false;
		return lastAction == BizConstant.ACTION_MAXIMIZE && getNumberOfFoundSolutions() > 0;
	}

	public boolean isScheduled() {
		if (lastAction == BizConstant.ACTION_UNKNOWN)
			return false;
		return lastAction == BizConstant.ACTION_SCHEDULE_ACTIVITIES && getNumberOfFoundSolutions() > 0;
	}

	/**
	 * This method is trying to post all constraints that are turned on and
	 * which status is unknown. The actual "post" is done by concrete
	 * implementation and can fail. If the post fails, the proper constraint's
	 * status is set as "violated" otherwise "satisfied". Posts all problem
	 * constraints that are turned on.
	 *
	 * Saves domains for all variables
	 *
	 * @return <b>true</b> if all constraints are posted successfully; <b>false</b>
	 *         if at least one constraint failed to be posted.
	 */
	public boolean postConstraints() {
		boolean result = true;
		for (int i = 0; i < getConstraints().length; i++) {
			BizConstraint c = getConstraints()[i];
			if (c.isOn()) {
				if (!c.post()) {
					c.setStatus(BizConstant.STATUS_VIOLATED);
					result = false;
				} else {
					c.setStatus(BizConstant.STATUS_SATISFIED);
				}
			}
		}
		saveDomains();
		return result;
	}

	public boolean isObjective(BizInt var) {
		return var.equals(objective);
	}

	public void setObjective(BizInt var) {
		objective = var;
		csp.setObjective(var.getVar());
	}

	public BizObject getObjective() {
		return objective;
	}

	/**
	 * This method if trying to find solutions for the CSP using a solving
	 * algorithm defined by a concrete CSP and a selected CP solver. The search
	 * can be limited by time, number of failed attempts, and other limitations
	 * such as "maxNumberOfSolutions". After execution of this method the found
	 * solution number is available via method getSolutionNumber() that could
	 * return 0 if findSolution fails. Sets the indicator lastAction to "FindSolution".
	 * @param solutionNumber
	 *            is an integer that is more or equal to 1.
	 * @return <b>true</b> if a solution with number "solutionNumber" is found or
	 * <b>false</b> if the problem has less than "solutionNumber" solutions.
	 */
	public Solution findSolution(int solutionNumber) {
		lastAction = BizConstant.ACTION_SOLVE;
		Solver solver = csp.getSolver();
		Solution[] solutions;
		if (numberOfFoundSolutions == 0 || solutionNumber == 0) {
			solutions = solver.findAllSolutions();
			numberOfFoundSolutions = solutions.length;
		}
		if (solutionNumber < numberOfFoundSolutions) {
			Solution solution = solver.getSolution(solutionNumber);
			BizInt[] bizVars = getIntegers();
			Var[] vars = solution.getVars();
			for (int i = 0; i < vars.length; i++) {
				bizVars[i].setValue(""+vars[i].getValue());
			}
			setSolutionNumber(solutionNumber);
			return solution;
		}
		else
			return null;
	}

	public Solution findLastSolution() {
		if (numberOfFoundSolutions == 0)
			findSolution(1);
		return findSolution(getNumberOfFoundSolutions()-1);
	}

	/**
	 * This method if trying to find a solution for the CSP using a solving
	 * algorithm defined by a concrete CSP and a selected CP solver. The search
	 * can be limited by time, number of failed attempts, and other limitations.
	 *
	 * @return <b>true</b> if a solution found or <b>false</b> if there are no solutions
	 */
	public Solution findSolution() {
		return csp.getSolver().findSolution();
	}

	/**
	 * This method saves string values of all constrained variables to be displayed
	 * when a solution is found
	 */
	public void saveSolution() {
		for(int i=0; i<getIntegers().length; ++i) {
			BizObject var = getIntegers()[i];
			var.saveValue();
		}
		// TODO the same for real and other CorkObjects
	}

	/**
	 * Saves domains for all variables
	 */
	public void saveDomains() {
		for(int i=0; i<getIntegers().length; ++i) {
			BizObject var = getIntegers()[i];
			var.saveDomain();
		}
		for (int i = 0; i < activities.size(); i++) {
			BizActivity act = (BizActivity)activities.elementAt(i);
			act.saveDomain();

		}
		// TODO the same for real and other CorkObjects
	}

	/**
	 * This method is trying to find a solution that minimizes the variable that
	 * was defined as the problem optimization objective. Uses a solving
	 * algorithm defined by a concrete CSP and a selected CP solver. The search
	 * can be limited by time, number of failed attempts, and other limitations.
	 *
	 * SIC! If used this method should be overloaded in the concrete
	 * implementation of the CP solver. Sets the indicator lastAction to
	 * "CorkConstant.ACTION_MINIMIZE".
	 *
	 * @param objective
	 *            that is an integer or real csp variable.
	 * @return a solution found
	 * @return null if there are no solutions
	 */
	public Solution minimize() {
		lastAction = BizConstant.ACTION_MINIMIZE;
		Solution solution = csp.getSolver().findOptimalSolution();
		if (solution != null) {
			BizInt[] bizVars = getIntegers();
			Var[] vars = solution.getVars();
			for (int i = 0; i < vars.length; i++) {
				bizVars[i].setValue(""+vars[i].getValue());
			}
			setSolutionNumber(solution.getSolutionNumber()+1);
			setNumberOfFoundSolutions(1);
		}
		return solution;
	}

	/**
	 * This method is trying to find a solution that maximizes the variable that
	 * was defined as the problem optimization objective. Uses a solving
	 * algorithm defined by a concrete CSP and a selected CP solver. The search
	 * can be limited by time, number of failed attempts, and other limitations.
	 *
	 * This method is implemented via minimize using an opposite (by sign)
	 * objective. Sets the indicator lastAction to "Maximize".
	 *
	 * @param objective
	 *            that is an integer or real constrained variable.
	 * @return <b>solution found</b>
	 * <b>null</b> if there are no solutions
	 *
	 */
	public Solution maximize() {
		lastAction = BizConstant.ACTION_MAXIMIZE;
		Solution solution = csp.maximize();
		if (solution != null) {
			BizInt[] bizVars = getIntegers();
			Var[] vars = solution.getVars();
			for (int i = 0; i < vars.length; i++) {
				bizVars[i].setValue(""+vars[i].getValue());
			}
			setSolutionNumber(solution.getSolutionNumber()+1);
			setNumberOfFoundSolutions(1);
		}
		return solution;
	}

	public Solution scheduleActivities() {
		lastAction = BizConstant.ACTION_SCHEDULE_ACTIVITIES;
		Solution solution = getSchedule().scheduleActivities();
		if (solution != null) {
//			for (int i = 0; i < getActivities().size(); i++) {
//				BizActivity act = (BizActivity)getActivities().elementAt(i);
//				act.setValue(act.toString());
//			}
			setSolutionNumber(0);
			setNumberOfFoundSolutions(1);
			return solution;
		}
		else
			return null;
	}

	public void displayVars() {
		log(getId()+" vars#: "+getIntegers().length);
		for(int i=0; i < getIntegers().length; i++) {
			BizInt var = (getIntegers()[i]);
			log(getId()+" vars["+i+"]: "+var.toString());
		}
	}

	public void displayConstraints() {
		log(getId()+" constraints#: "+getConstraints().length);
		for(int i=0; i < getConstraints().length; i++) {
			BizConstraint ct = (getConstraints()[i]);
			log("BizConstraint["+i+"]: "+ct.getName());
		}
	}

	public boolean isFindSolutionVisible() {
		return findSolutionVisible;
	}

	public void setFindSolutionVisible(boolean findSolutionVisible) {
		this.findSolutionVisible = findSolutionVisible;
	}

	public boolean isMaximizeVisible() {
		return maximizeVisible;
	}

	public void setMaximizeVisible(boolean maximizeVisible) {
		this.maximizeVisible = maximizeVisible;
	}

	public boolean isMinimizeVisible() {
		return minimizeVisible;
	}

	public void setMinimizeVisible(boolean minimizeVisible) {
		this.minimizeVisible = minimizeVisible;
	}

	public void setScheduleActivitiesVisible(boolean visible) {
		this.scheduleActivitiesVisible = visible;
	}

	public boolean isScheduleActivitiesVisible() {
		return scheduleActivitiesVisible;
	}

	public int getLastAction() {
		return lastAction;
	}

	public void setLastAction(int lastAction) {
		this.lastAction = lastAction;
	}

	/**
	 * Log the string parameter
	 */
	public void log(String text) {
		csp.log(text);
	}

	/**
	 * Report an error using a string parameter
	 */
	public void err(String text) {
		csp.log(text);
	}

	/**
	 * This method is similar to postConstraints but it throws a runtime
	 * exception if if at least one constraint failed to be posted.
	 *
	 * @return <b>true</b> if all constraints are posted successfully; <b>false</b>
	 */
	public boolean activateConstraints() {
		boolean result = true;
		for (int i = 0; i < getConstraints().length; i++) {
			BizConstraint c = getConstraints()[i];
			if (c.isOn()) {
				if (!c.post()) {
					c.setStatus(BizConstant.STATUS_VIOLATED);
					result = false;
					throw new RuntimeException("Failure to post "+c.getName());
				} else {
					c.setStatus(BizConstant.STATUS_SATISFIED);
				}
			}
		}
		return result;
	}

	/**
	 * The method "activate" is used in the problem definition. It safely posts
	 * this constraint without necessity to check if the posting was successful.
	 * If posting fails, this method throws a runtime exception.
	 *
	 * @throws a
	 *             RuntimeException if a failure happens during the posting.
	 * @return true or false
	 */
	public boolean activate(BizConstraint c) {
		if (!c.post())
			throw new RuntimeException("Failure to post "+c.getName());
		return true;
	}

}
