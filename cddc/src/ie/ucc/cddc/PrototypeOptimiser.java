package ie.ucc.cddc;

import com.openrules.ruleengine.OpenRulesEngine;

public class PrototypeOptimiser {

	public static void main(String[] args) {
		String fileName = "file:rules/Environment.xls";
		OpenRulesEngine engine = new OpenRulesEngine(fileName);

		engine.run("setupData");
		engine.run("executeDiskUtilisationRule");
	}	
}
