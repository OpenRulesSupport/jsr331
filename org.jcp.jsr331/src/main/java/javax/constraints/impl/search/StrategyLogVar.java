package javax.constraints.impl.search;

import java.util.Calendar;

import javax.constraints.Var;

public class StrategyLogVar extends AbstractSearchStrategy {
	String text;
	Var var;
	
	public StrategyLogVar(String text, Var var) {
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
