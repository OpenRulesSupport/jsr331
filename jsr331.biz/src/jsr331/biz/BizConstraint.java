package jsr331.biz;

import javax.constraints.Constraint;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2207
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/

/**
 * This class represent a business view of any constraint that may have multiple
 * implementations in different CP solvers. <br>
 * A constraint can be turned on or turned off using methods turnOn() and turnOff()
 * correspondingly. Use method isOn() to check if a constraint is turned on. <br>
 * A turned on constraint can have different statuses:
 * <ol>
 * <li> unknown (was not posted yet)
 * <li> satisfied (successfully posted)
 * <li> violated or partially violated (unsuccessfully posted)
 * </ol>
 *
 * By default every constraint is either satisfied or violated. However, you can set
 * maximal constraint violation more than 0 (and less or equal 1) to allow partial
 * constraint violations. You can define maximally allowed constraint violation
 *
 *
 * @author JF
 *
 */

public class BizConstraint implements IBizConstraint {

	private Constraint 	constraint;
	private final BizProblem		problem;
	private String 			name;
	private int 			status;
	private double 			maxViolation; // 0..1
	private double 			violation;
	private double 			importance;
	private String 			info;
	private int 			savedStatus;



	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	/**
	 * Posts this constraint
	 */
	public boolean post() {
		try {
			constraint.post();
			setStatus(BizConstant.STATUS_SATISFIED);
			return true;
		}
		catch(Exception e) {
			setStatus(BizConstant.STATUS_VIOLATED);
			return false;
		}
	}

	public BizConstraint(BizProblem csp) {
		this(csp,"constraint");
	}

	/**
	 * Constructs an instance of BizConstraint with the name "name",
	 * status STATUS_UNKNOWN, importance 1, that cannot
	 * be violated (maxViolation = 0).
	 * @param csp a CorkProblem
	 * @param name a String
	 */
	public BizConstraint(BizProblem csp, String name) {
		this.problem = csp;
		this.name = name;
		setStatus(BizConstant.STATUS_UNKNOWN);
		saveStatus();
		importance = 1;
		maxViolation = 0; // can not be violated
		violation = 0;
		setInfo(name);
	}

	/**
	 * Based on an instance of CorkConstraint this constructor creates
	 * an instance of BizConstraint with the name "name",
	 * status STATUS_UNKNOWN, importance 1, that cannot
	 * be violated (maxViolation = 0).
	 * @param csp a CorkProblem
	 * @param name a String
	 */
	public BizConstraint(BizProblem csp, Constraint constraint, String name) {
		this(csp,name);
		setConstraint(constraint);
	}

	public BizProblem getProblem() {
		return problem;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSavedStatus() {
		return savedStatus;
	}

	public void setSavedStatus(int savedStatus) {
		this.savedStatus = savedStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isOn() {
		return getStatus() != BizConstant.STATUS_OFF;
	}

	public boolean isOff() {
		return getStatus() == BizConstant.STATUS_OFF;
	}

	public void turnOn() {
		setStatus(BizConstant.STATUS_UNKNOWN);
	}

	public void turnOff() {
		setStatus(BizConstant.STATUS_OFF);
	}

	public boolean isUnknown() {
		return getStatus() == BizConstant.STATUS_UNKNOWN;
	}
	public boolean isViolated() {
		return getStatus() == BizConstant.STATUS_VIOLATED;
	}

	public boolean canBeViolated() {
		return getMaxViolation() > 0;
	}

	public boolean isPartiallyViolated() {
		return getStatus() == BizConstant.STATUS_PARTIALLY_VIOLATED;
	}

	public boolean isSatisfied() {
		return getStatus() == BizConstant.STATUS_SATISFIED;
	}

	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	public double getMaxViolation() {
		return maxViolation;
	}

	public void setMaxViolation(double maxViolation) {
		if (maxViolation < 0 || maxViolation > 1)
			getProblem().log("Maximal violation cannot be  less than 0 or more than 1");
		else
			this.maxViolation = maxViolation;
	}

	public double getViolation() {
		return violation;
	}

	public void setViolation(double violation) {
		this.violation = violation;
	}

	public void saveStatus() {
		savedStatus = getStatus();
	}

	public void resetStatus() {
		setStatus(savedStatus);
	}

	public String showStatus() {
		switch(getStatus()) {
		case BizConstant.STATUS_OFF: return "Off";
		case BizConstant.STATUS_UNKNOWN: return "Unknown";
		case BizConstant.STATUS_VIOLATED: return "Violated";
		case BizConstant.STATUS_PARTIALLY_VIOLATED: return "Partially Violated";
		case BizConstant.STATUS_SATISFIED: return "Satisfied";
		default: return "?";
		}
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}


}
