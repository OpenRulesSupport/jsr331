package javax.constraints.impl.search;

import java.util.Calendar;

import javax.constraints.VarReal;

public class StrategyLogVarReal extends AbstractSearchStrategy {
	String text;
	VarReal var;
	
	public StrategyLogVarReal(String text, VarReal var) {
		super(var.getProblem().getSolver());
		this.text = text;
		this.var = var;
		setType(SearchStrategyType.CUSTOM);
	}
	
	public boolean run() {
		getProblem().log(text + var + ". " + Calendar.getInstance().getTime());
		return true;
	}

}
