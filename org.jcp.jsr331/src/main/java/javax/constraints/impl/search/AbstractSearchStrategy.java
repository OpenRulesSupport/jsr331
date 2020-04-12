//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints.impl.search;

import java.util.ArrayList;
import java.util.Vector;

import javax.constraints.SearchStrategy;
import javax.constraints.Solver;
import javax.constraints.ValueSelector;
import javax.constraints.ValueSelectorType;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.VarSelector;
import javax.constraints.VarSelectorType;
import javax.constraints.VarSet;
import javax.constraints.impl.BasicVarSet;
import javax.constraints.impl.CommonBase;
import javax.constraints.impl.AbstractProblem;
import javax.constraints.impl.search.selectors.ValueSelectorMax;
import javax.constraints.impl.search.selectors.ValueSelectorMin;
import javax.constraints.impl.search.selectors.ValueSelectorMinMaxAlternate;
import javax.constraints.impl.search.selectors.VarSelectorInputOrder;
import javax.constraints.impl.search.selectors.VarSelectorMinDomain;
import javax.constraints.impl.search.selectors.VarSelectorMinDomainMinValue;
import javax.constraints.impl.search.selectors.VarSelectorRandom;

abstract public class AbstractSearchStrategy extends CommonBase implements SearchStrategy {
	
	Solver solver;
	protected Var[] vars;
	protected VarReal[] varReals;
//	protected VarSet[] varSets;
	protected VarSelector varSelector;
	protected ValueSelector valueSelector;
	SearchStrategyType type;
	
	public AbstractSearchStrategy(Solver solver) {
		super(solver.getProblem());
		this.solver = solver;
		varSelector = null;
		valueSelector = new ValueSelectorMin();
		type = SearchStrategyType.DEFAULT;
		vars = getProblem().getVars();
	}

	@Override
	public Solver getSolver() {
		return solver;
	}

	public SearchStrategyType getType() {
		return type;
	}
	
	public void setType(SearchStrategyType type) {
		this.type = type;
	}

	public Var[] getVars() {
		return vars;
	}

	public void setVars(Var[] vars) {
		this.vars = vars;
	}
	
	/**
	 * Defines an array of integer variables that may be used (or not) by this strategy
	 * @param vars a list of integer constrained variables
	 */
	public void setVars(ArrayList<Var> vars) {
		setVars((Var[])vars.toArray(new Var[vars.size()]));
	}
	
	/**
	 * Defines an array of set variables that may be used (or not) by this strategy.
	 * These variables are extracted from the array of set variables passed as a parameter
	 * @param setVars an array of set variables
	 */
	public void setVars(VarSet[] setVars) {
		Vector intVars = new Vector();
		for (int i = 0; i < setVars.length; i++) {
			BasicVarSet setVar = (BasicVarSet)setVars[i];
			Var[] requiredVars = setVar.getRequiredVars();
			for (int j = 0; j < requiredVars.length; j++) {
				intVars.add(requiredVars[j]);
			}
			intVars.add(setVar.getCardinality());
		}
		Var[] searchVars = new Var[intVars.size()];
		for (int i = 0; i < intVars.size(); i++) {
			searchVars[i] = (Var)intVars.elementAt(i);
		}
		setVars(searchVars);
	}

	public ValueSelector getValueSelector() {
		return valueSelector;
	}

	@Override
	public VarSelector getVarSelector() {
		return varSelector;
	}

	public void setValueSelector(ValueSelector valueSelector) {
		this.valueSelector = valueSelector;
	}

	@Override
	public void setVarSelector(VarSelector varSelector) {
		this.varSelector = varSelector;
	}
	
	/**
	 * Sets a variable selector type to be used by this strategy
	 * @param varSelectorType VarSelectorType
	 */
	public void setVarSelectorType(VarSelectorType varSelectorType) {
		switch (varSelectorType) {
		case INPUT_ORDER:
			setVarSelector(new VarSelectorInputOrder(this));
			break;
		case MIN_DOMAIN:
			setVarSelector(new VarSelectorMinDomain(this));
			break;
		case MIN_DOMAIN_MIN_VALUE:
			setVarSelector(new VarSelectorMinDomainMinValue(this));
			break;
		case RANDOM:
			setVarSelector(new VarSelectorRandom(this));
			break;
		default:
			setVarSelector(new VarSelectorInputOrder(this));
			break;
		}
	}
	
	/**
	 * Sets a value selector type to be used by this strategy
	 * @param valueSelectorType ValueSelectorType
	 */
	public void setValueSelectorType(ValueSelectorType valueSelectorType) {
		switch (valueSelectorType) {
		case MAX:
			setValueSelector(new ValueSelectorMax());
			break;
		case MIN:
			setValueSelector(new ValueSelectorMin());
			break;
		case MIN_MAX_ALTERNATE:
			setValueSelector(new ValueSelectorMinMaxAlternate());
			break;

		default:
			setValueSelector(new ValueSelectorMin());
			break;
		}
	}
	

	@Override
	public void trace() {
		AbstractProblem p = (AbstractProblem)getProblem();
		p.notImplementedException("SearchStrategy method trace()");
	}

	public VarReal[] getVarReals() {
		return varReals;
	}

	public void setVarReals(VarReal[] varReals) {
		this.varReals = varReals;
	}

//	public VarSet[] getVarSets() {
//		return varSets;
//	}
//
//	public void setVarSets(VarSet[] varSets) {
//		this.varSets = varSets;
//	}
	
	/**
	 * This method is used by CUSTOM strategies such as SearchStrategyLog
	 * to define its execution logic
	 * @return boolean
	 */
	public boolean run() {
		getProblem().log("This method is used only by CUSTOM strategies in which it is overridden");
		return true;
	}

}
