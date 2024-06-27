package pricing;

/**
 * A simple Java launcher for Rules.xls with
 * data defined in Java objects
 * 
 * @author jf 
 */

import com.openrules.ruleengine.Decision;

public class MainMany {

	public static void main(String[] args) {
		
		String fileName = "file:rules/Decision.xls";
		System.setProperty("OPENRULES_MODE", "Solve");
		Decision decision = new Decision("DecisionAllocateAnalystsToCases",fileName);
		decision.put("trace","On");
		decision.put("report","On");
		decision.put("MaxSolutions", "10");
		decision.execute();
	}
}
