package cpinside.openrules;

import com.openrules.ruleengine.OpenRulesEngine;

/**
 * A simple Java test for cpinside.openrules interface
 *
 */

public class CorkOperTest {

	public static void main(String[] args) {
		String fileName = "file:rules/main/MainXYZ.xls";
		String methodName = "main";
		OpenRulesEngine engine = new OpenRulesEngine(fileName);
		System.out.println("\n=====================================================\n"
						+ "OpenRulesEngine: " + fileName
						+ "\n=====================================================\n");
		engine.run(methodName);
	}
}
