package javax.constraints.linear;

import javax.constraints.impl.Var;
import javax.constraints.impl.VarReal;

public class ComparableVariable implements Comparable {

	Var var;
	VarReal varReal;
	String type = null;
	
	/**
	 * Creates a new LP integer Variable based on a constrained var
	 * @param var a integer variable
	 */
	public ComparableVariable(Var var) {
		this.var = var;
		type = "Var";
	}
	
	/**
	 * Creates a new LP real Variable based on a constrained var
	 * @param var a real constrained variable
	 */
	public ComparableVariable(VarReal var) {
		this.varReal = var;
		type = "VarReal";
	}

	/**
	 * Used for sorting arrays of LpVariables by their names
	 * @param obj an object to compare with
	 * @return an integer
	 */
	public int compareTo(Object obj)
	{
		ComparableVariable compareVar = (ComparableVariable)obj;
		return getName().compareTo(compareVar.getName());
	}
	
	/**
	 * 
	 * @return a variable name
	 */
	public String getName() {
		if (isInteger())
			return var.getName();
		else
			return varReal.getName();
	}
	
	/**
	 * 
	 * @return true if this is an integer constrained variable
	 */
	public boolean isInteger() {
		return "Var".equals(type);
	}
	
	/**
     * 
     * @return true if this is an real constrained variable
     */
	public boolean isReal() {
		return "VarReal".equals(type);
	}
	
	/**
	 * 
	 * @return an integer constrained variable
	 */
	public Var getVar() {
		return var;
	}
	
	/**
     * 
     * @return a real constrained variable
     */
	public VarReal getVarReal() {
		return varReal;
	}


}
