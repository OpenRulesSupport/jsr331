package com.openrules.pricing;


import javax.constraints.Var;
import javax.constraints.impl.search.AbstractSearchStrategy;

public class StrategyAssignVarValue extends AbstractSearchStrategy {
	int value;
	Var var;
	
	public StrategyAssignVarValue(Var var,int value) {
		super(var.getProblem().getSolver());
		this.value = value;
		this.var = var;
		setType(SearchStrategyType.CUSTOM);
	}
	
	public boolean run() {
		getProblem().post(var,"=",value);
		return true;
	}

}
