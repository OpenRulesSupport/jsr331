package javax.constraints.impl.search;

import javax.constraints.impl.search.Solver;
import javax.constraints.ValueSelector;
import javax.constraints.ValueSelectorType;
import javax.constraints.VarSelector;
import javax.constraints.VarSelectorType;
import javax.constraints.impl.Problem;

import JSetL.IntLVar;
import JSetL.LabelingOptions;
import JSetL.SetLVar;
import JSetL.SolverClass;
import JSetL.ValHeuristic;
import JSetL.VarHeuristic;

/**
 * Implements the JSR331 search strategy extending the common 
 * implementation AbstractSearchStrategy. The implementation is base on the
 * solver JSetL.
 * 
 * <p>To support the implementation of set variables a new array of VarSets
 * was added to the class, like AbstractSearchStrategy do for Var and
 * VarReal.
 * 
 * @author Fabio Biselli
 *
 */
public class SearchStrategy extends AbstractSearchStrategy {

	/**
	 * The strategy set variables. Which are the problem set variables
	 * by default, and can be specified with the setter method.
	 */
	protected javax.constraints.VarSet[] varSets;
	
	/**
	 * Build a new SearchStrategy. Adding the set variables of the
	 * problem to <code>this.varSets</code>.
	 * 
	 * @param solver the Solver which the search strategy is related.
	 */
	public SearchStrategy(Solver solver) {
		super(solver);
		varSets = ((Problem) getProblem()).getSetVars();
	}

	/**
	 * Auxiliary method that maps Variable Selectors and Value Selectors
	 * to the JSetL heuristics. A new JSetL LabelingOptions is build and
	 * set to match the user request to customize the search strategy.
	 * 
	 * <p>Some JSetL heuristic are not yet supported by the standard interface
	 * JSR331:
	 * <br>Variable selector:
	 * <ul>
	 * <li>MID_MOST,
	 * <li>RIGHT_MOST..
	 * </ul>
     * <br>Value selector:
	 * <ul>
	 * <li>MID_RANDOM,
	 * <li>RANGE_RANDOM.
	 * </ul>
	 * 
	 * @return a new LabelingOptions representing the selected heuristic.
	 */
	private LabelingOptions getHeuristic() {
		LabelingOptions lop = new LabelingOptions();
		VarSelector varSelector = getVarSelector();
		if (varSelector != null) {
			VarSelectorType varType = varSelector.getType();
			switch(varType) {
			case INPUT_ORDER: 
				lop.var = VarHeuristic.LEFT_MOST;
				break;
			case MIN_VALUE:
				lop.var = VarHeuristic.MIN;
				break;
			case MAX_VALUE:
				lop.var = VarHeuristic.MAX;
				break;
			case RANDOM:
				lop.var = VarHeuristic.RANDOM;
				break;
			case MIN_DOMAIN:
				lop.var = VarHeuristic.FIRST_FAIL;
				break;
			default:
				lop.var = VarHeuristic.LEFT_MOST;
				break;
			}
		}
		else lop.var = VarHeuristic.LEFT_MOST;
		ValueSelector valueSelector = getValueSelector();
		if (valueSelector != null) {
			ValueSelectorType valueType = valueSelector.getType();
			switch(valueType) {
			case MIN:
				lop.val = ValHeuristic.GLB;
				break;
			case MAX:
				lop.val = ValHeuristic.LUB;
				break;
			case MIDDLE:
				lop.val = ValHeuristic.MID_MOST;
				break;
			case MEDIAN:
				lop.val = ValHeuristic.MEDIAN;
				break;
			case RANDOM:
				lop.val = ValHeuristic.EQUI_RANDOM;
				break;
			default:
				lop.val = ValHeuristic.GLB;
				break;
			}
		}
		else lop.val = ValHeuristic.GLB;
		return lop;
	}
	
	/**
	 * Auxiliary method that apply a label to the integer variables
	 * implementation (JSetL.IntLVar) identified in the strategy.
	 */
	protected void label() {
		if (vars == null || vars.length == 0) {
			return;
		}
		SolverClass sc = ((Solver) getSolver()).getSolverClass();
		IntLVar[] vec = new IntLVar[vars.length];
		for (int i = 0; i < vars.length; i++) {
			vec[i] = (IntLVar) vars[i].getImpl();
		}
		sc.add(IntLVar.label(vec, getHeuristic()));
	}
	
	/**
	 * Auxiliary method that apply a label to the set variables
	 * implementation (JSetL.SetLVar) identified in the strategy.
	 */
	protected void labelSets() {
		if (varSets == null || varSets.length == 0)
			return;
		SolverClass sc = ((Solver) getSolver()).getSolverClass();
		SetLVar[] vec = new SetLVar[varSets.length];
		for (int k = 0; k < varSets.length; k++) 
			vec[k] = (SetLVar) varSets[k].getImpl();
		sc.add(SetLVar.label(vec, getHeuristic()));
	}

	/**
	 * Defines an array of set variables that may be used (or not) by this 
	 * strategy.
	 * 
	 * @param vars the array of set variables.
	 */
	public void setVars(javax.constraints.VarSet[] vars) {
		this.varSets =  vars;
	}
	
	/**
	 * Getter method for the array of set variables saved into the strategy.
	 * 
	 * @return the set variables array.
	 */
	public javax.constraints.VarSet[] getVarSet() {
		return this.varSets;
	}
}

