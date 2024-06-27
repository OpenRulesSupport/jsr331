package javax.constraints.impl.search;


import choco.kernel.solver.variables.Var;

import javax.constraints.SearchStrategy;

public abstract class VarSelectorI extends javax.constraints.impl.search.selectors.AbstractVarSelector 
									implements choco.kernel.solver.branch.VarSelector{
	
	public VarSelectorI(SearchStrategy strategy) {
		super(strategy);
		// TODO Auto-generated constructor stub
	}

	@Override
	public final Var selectVar() {
		int idx = select();
		return ((javax.constraints.impl.Var)getVars()[idx]).getChocoDomainVar();
	}

}
