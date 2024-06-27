package pricing;

import com.openrules.ruleengine.Decision;

public class MainOptimal {

	public static void main(String[] args) {
		
		String fileName = "file:rules/Decision.xls";
		System.setProperty("OPENRULES_MODE", "Solve");
		Decision decision = new Decision("DecisionAllocateAnalystsToCases",fileName);
		decision.put("MaxSolutions", "30");
		decision.put("Minimize","Total Cost");
		//decision.put("Maximize","Total Cost");
		decision.execute();
	}
}


