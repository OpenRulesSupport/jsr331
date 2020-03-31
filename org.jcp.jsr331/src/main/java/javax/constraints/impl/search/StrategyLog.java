package javax.constraints.impl.search;

import java.util.Calendar;

import javax.constraints.Solver;

public class StrategyLog extends AbstractSearchStrategy {
	String text;
	
	public StrategyLog(Solver solver, String text) {
		super(solver);
		this.text = text;
		setType(SearchStrategyType.CUSTOM);
	}
	
	public boolean run() {
		getProblem().log(text + " at " + Calendar.getInstance().getTime());
		return true;
	}

}
