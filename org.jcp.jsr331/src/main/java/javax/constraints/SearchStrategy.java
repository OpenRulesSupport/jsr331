//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;

import java.util.ArrayList;

public interface SearchStrategy {
	
	static public enum SearchStrategyType {
		DEFAULT,
		GOAL,
		CUSTOM
	}
	
	/**
	 * Returns a solver to which this strategy belongs.
	 * @return a solver to which this strategy belongs.
	 */
	public Solver getSolver();

	/**
	 * Returns the name of this strategy.
	 * @return the name of this strategy.
	 */
	public String getName();

	/**
	 * Sets the name of this strategy.
	 * @param name the name for this strategy.
	 */
	public void setName(String name);
	
	/**
	 * Returns a type of this Search Strategy
	 * @return SearchStrategyType
	 */
	public SearchStrategyType getType();
	
	/**
	 * Sets a type of this Search Strategy
	 * @param type SearchStrategyType
	 */
	public void setType(SearchStrategyType type);

	/**
	 * Defines an array of integer variables that may be used (or not) by this strategy
	 * @param vars an array of integer constrained variables
	 */
	public void setVars(Var[] vars);
	
	/**
	 * Defines an array of integer variables that may be used (or not) by this strategy
	 * @param vars a list of integer constrained variables
	 */
	public void setVars(ArrayList<Var> vars);
	
	
	/**
	 * Returns a an array of integer variables that is in use by this strategy
	 * @return an array of variables
	 */
	public Var[] getVars();
	
	/**
	 * Defines an array of integer variables that may be used (or not) by this strategy.
	 * These variables are extracted from the array of set variables passed as a parameter
	 * @param setVars an array of set variables
	 */
	public void setVars(VarSet[] setVars);
	
	/**
	 * Sets a variable selector that may be used (or not) by this strategy
	 * @param varSelector VarSelector
	 */
	public void setVarSelector(VarSelector varSelector);
	
	/**
	 * Sets a variable selector type to be used by this strategy
	 * @param varSelectorType VarSelectorType
	 */
	public void setVarSelectorType(VarSelectorType varSelectorType);
	
	/**
	 * Returns a variable selector that was set for this strategy
	 * @return VarSelector
	 */
	public VarSelector getVarSelector();
	
	/**
	 * Sets a value selector that may be used (or not) by this strategy
	 * @param valueSelector ValueSelector
	 */
	public void setValueSelector(ValueSelector valueSelector);
	
	/**
	 * Sets a value selector type to be used by this strategy
	 * @param valueSelectorType ValueSelectorType
	 */
	public void setValueSelectorType(ValueSelectorType valueSelectorType);
	
	/**
	 * @return a value selector that was set for this strategy
	 */
	public ValueSelector getValueSelector();

	/**
	 * This method should be defined by a concrete solver
	 * implementation. This method returns an Object that represents an actual
	 * implementation of this strategy inside an underlying selected solver.
	 * @return an Object that represents an actual implementation of this strategy
	 *         inside an underlying selected solver.
	 */
	public Object getImpl();

	/**
	 * This method defines an Object that represents an actual
	 * implementation of this strategy inside a selected solver.
	 * @param impl the Object that represents an actual implementation of this
	 *             strategy inside a selected solver.
	 */
	public void setImpl(Object impl);
	
	/**
	 * Forces the solver to log information about this strategy every time when it is executed
	 */
	public void trace();
	
	/**
	 * @return a an array of real variables that is in use by this strategy
	 */
	public VarReal[] getVarReals();
	
	/**
	 * Defines an array of real variables that may be used (or not) by this strategy
	 * @param varReals an array of real variables
	 */
	public void setVarReals(VarReal[] varReals);

//	/**
//	 * Returns a an array of set variables that is in use by this strategy
//	 */
//	public VarSet[] getVarSets();

//	/**
//	 * Defines an array of set variables that may be used (or not) by this strategy
//	 * @param varSets
//	 */
//	public void setVarSets(VarSet[] varSets);
	
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
	 * This method is used by CUSTOM strategies such as SearchStrategyLog
	 * to define its execution logic
	 * @return boolean
	 */
	public boolean run();
 
	
}

