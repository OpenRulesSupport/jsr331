package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/
/**
 * Defines a generic interface for constrains 
 * @see BizConstraint
 * @author JF
 */

public interface IBizConstraint {
	
	public BizProblem getProblem();
	public String getName();
	public void setName(String name);
	public int getStatus();
	public void setStatus(int status);
	public String getInfo();
	public boolean isOn();
	public boolean isOff();
	public void turnOn();
	public void turnOff();
	public boolean isUnknown();
	public boolean isViolated();
	public boolean isPartiallyViolated();
	public boolean isSatisfied();
	public boolean canBeViolated();
	public double getMaxViolation();
	public double getViolation();
	public double getImportance();
	public boolean post();
	
}
