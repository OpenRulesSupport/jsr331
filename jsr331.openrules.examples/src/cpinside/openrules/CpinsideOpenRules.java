package cpinside.openrules;

/**
 * A simple Java test for cpinside.openrules interface
 *
 */

import com.openrules.ruleengine.OpenRulesEngine;

public class CpinsideOpenRules {

	public static void main(String[] args)
	{
		//String fileName = "file:rules/main/MainXYZ.xls";
		String fileName = "file:rules/main/MainSendMoreMoney.xls";
		String methodName = "main";
		if (args.length > 1)
		{
			fileName = args[0];
			methodName = args[1];
		}
		OpenRulesEngine engine = new OpenRulesEngine(fileName);
		System.out.println(
		"\n=====================================================\n" +
		   "OpenRulesEngine: " + fileName +
		"\n=====================================================\n");
		engine.run(methodName);
	}
}
