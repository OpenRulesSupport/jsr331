package pricing;

import javax.constraints.Problem;
import javax.constraints.Var;


public class SaleVar {
	
	SaleRecord record;
	
	int[]	domain;
//	int[] 	expectedRevenueArray;
	int[]	expectedNumUnitsSoldArray;
	
	// unknown
	Var priceVar;
	Var expectedRevenueVar;
	Var expectedNumUnitsSoldVar;
	Var marginVar;
	
//	expected_revenue	is	suggested_sale_price	*	predicted_num_units_sold				Optimization is done on sale_id level. Different sales are independent
//	sell_through	is	predicted_num_units_sold	/	num_units_available				1
//	margin	is	(suggested_sale_price - base)	/	suggested_sale_price				
//	discount	is 1 - (	suggested_sale_price	/	msrp_price	)			
//									
//	discount	>=	20%			Don't sell to close to MSRP			
//	margin	>=	40%			Ensure minimum margin			
//	suggested_sale_price	<= 	1.2	*	orig_sale_price	Don't increase price for more than 20% over human			
//	sell_through	>=	20%			Sell more units		
	
	public SaleVar(Problem p, SaleRecord record) {
		this.record = record;
		domain = record.getDomain();
		expectedNumUnitsSoldArray = record.getExpectedNumUnitsSoldArray();
//		expectedRevenueArray = record.getExpectedRevenueArray();
		
		
		//expectedRevenueVar = p.element(expectedRevenueArray, priceVar);
		//expectedNumUnitsSoldVar = p.element(expectedNumUnitsSoldArray, priceVar);
		int min = domain[0];
		int max = domain[domain.length-1];
		priceVar = p.variable(record.getUniqueId()+"price",min,max);
		
		min = expectedNumUnitsSoldArray[0];
		max = expectedNumUnitsSoldArray[expectedNumUnitsSoldArray.length-1];
		expectedNumUnitsSoldVar = p.variable(record.getUniqueId()+"NumSold",min,max);
		
//		min = expectedRevenueArray[0];
//		max = expectedRevenueArray[expectedRevenueArray.length-1];
//		expectedRevenueVar = p.variable(record.getUniqueId()+"revenue",min,max);
		expectedRevenueVar = priceVar.multiply(expectedNumUnitsSoldVar);
		expectedRevenueVar.setName(record.getUniqueId()+"revenue");
		
		for (int i = 0; i < domain.length; i++) {
			p.postIfThen(p.linear(priceVar,"=",domain[i]), p.linear(expectedNumUnitsSoldVar, "=", expectedNumUnitsSoldArray[i]));
			//p.postIfThen(p.linear(priceVar,"=",domain[i]), p.linear(expectedRevenueVar, "=", expectedRevenueArray[i])) ;
		}
	}
		
		
	public SaleRecord getRecord() {
		return record;
	}
	public void setRecord(SaleRecord record) {
		this.record = record;
	}
		

}
