package pricing;

import java.util.ArrayList;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;

import com.exigen.ie.tools.Log;
import com.openrules.ruleengine.OpenRulesEngine;

public class Main {

	OpenRulesEngine engine;
	InputRecord[] inputRecords;
	ArrayList<SaleRecord> saleRecords;
	Problem problem;

	public Main() {
		long start = System.currentTimeMillis();
		Log.info("Create Engine to Read Data");
		String fileName = "file:rules/DecisionPricing.xls";
		engine = new OpenRulesEngine(fileName);
		Log.info("Created Engine in " + (System.currentTimeMillis() - start) + " ms");
		readData();
	}
	
	public void readData() {
		long start = System.currentTimeMillis();
		Log.info("Read Input Records...");
		inputRecords = (InputRecord[]) engine.run("getInputRecords");
		Log.info("Read " + inputRecords.length + " input records in " + (System.currentTimeMillis() - start) + " ms");
		
		start = System.currentTimeMillis();
		Log.info("Create Sale Records...");
		saleRecords = new ArrayList<SaleRecord>();
		int n = -1;
		for (int i = 0; i < inputRecords.length; i++) {
			SaleRecord saleRecord = null;
			if (n >= 0)
				saleRecord = saleRecords.get(n);
			InputRecord nextInputRecord = inputRecords[i];
			if (saleRecord == null || !saleRecord.isSameAs(nextInputRecord)) {
				String id = "SaleRecord-" + (saleRecords.size() + 1);
				saleRecord = new SaleRecord(id, nextInputRecord);
				saleRecords.add(saleRecord);
				n++;
			}
			saleRecord.addInputRecord(nextInputRecord);
		}
		Log.info("Created " + saleRecords.size() + " sale records in " + (System.currentTimeMillis() - start) + " ms");
		
	}

//	public boolean isAnalystCanBeAssignedToCase(Analyst a, Case c) {
//
//		Decision decision = new Decision("DefineIfAnalystCanBeAssignedToCase",
//				engine);
//		decision.put("Analyst", a);
//		decision.put("Case", c);
//		Result result = new Result();
//		decision.put("Result", result);
//		decision.execute();
//		return result.isAnalystCanBeAssignedToCase();
//
//	}

	public void defineCSP() throws Exception {
		problem = ProblemFactory.newProblem("Pricing1");
		Log.info("Create Constrained Variables...");
		SaleVar[] saleVars = new SaleVar[saleRecords.size()];
		for (int i = 0; i < saleRecords.size(); i++) {
			SaleRecord record = saleRecords.get(i);
			//Log.info("Create variables for " + record);
			saleVars[i] = new SaleVar(problem,record);
		}
		
//		expected_revenue	is	suggested_sale_price	*	predicted_num_units_sold				Optimization is done on sale_id level. Different sales are independent
//		sell_through	is	predicted_num_units_sold	/	num_units_available				1
//		margin	is	(suggested_sale_price - base)	/	suggested_sale_price				
//		discount	is 1 - (	suggested_sale_price	/	msrp_price	)			
//										
//		discount	>=	20%			Don't sell to close to MSRP			
//		margin	>=	40%			Ensure minimum margin			
//		suggested_sale_price	<= 	1.2	*	orig_sale_price	Don't increase price for more than 20% over human			
//		sell_through	>=	20%			Sell more units			
	
	}

	public void solveCSP() {
//		Solver solver = schedule.getSolver();
//		solver.setSearchStrategy(schedule.strategyScheduleActivities());
//		solver.addSearchStrategy(schedule.strategyAssignResources());
//
//		Solution solution = solver.findSolution();
//		if (solution == null)
//			schedule.log("No solutions");
//		else {
//			schedule.log(solution);
//		}
//		solver.logStats();
	}

	public static void main(String args[]) throws Exception {
		long start = System.currentTimeMillis();
		Main pricing = new Main();
		pricing.defineCSP();
		pricing.solveCSP();
		System.out.println("Total elapsed time: " + (System.currentTimeMillis() - start) + " ms");
	}
}
