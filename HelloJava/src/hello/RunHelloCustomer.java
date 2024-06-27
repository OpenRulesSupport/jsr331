package hello;

/**
 * A simple Java test for HelloCustomer.xls
 * 
 * @author jf 
 */
 
import java.util.ArrayList;
import java.util.List;

import com.openrules.ruleengine.OpenRulesEngine;

public class RunHelloCustomer {

	public static void main(String[] args)
	{
		String fileName = "file:rules/main/HelloCustomer.xls";
		String methodName = "helloCustomer";
		OpenRulesEngine engine = new OpenRulesEngine(fileName);
		System.out.println( 
		"\n=====================================================\n" +
		   "OpenRulesEngine: " + fileName + 
		"\n=====================================================\n");
		Customer customer = new Customer();
		customer.setName("Robinson");
		customer.setGender("Female");
		customer.setMaritalStatus("Married");
		Address addr1 = new Address();
		addr1.setState("AZ");
		Address addr2 = new Address();
		addr2.setState("NV");
		List<Address> addresses = new ArrayList<Address>();
		addresses.add(addr1);
		addresses.add(addr2);
		customer.setAddresses(addresses); 
		
		Response response = new Response();
		Object[] objects = new Object[] { customer, response };
		engine.run(methodName,objects);
		System.out.println("From Java : " + 
		 		 response.getMap().get("greeting") + ", " +
				 response.getMap().get("salutation") + 
				 customer.getName() + "!"		 					
		 		);
		String error = (String) response.getMap().get("error");
		if (!"NONE".equals(error))
			System.out.println("Validation ERROR: " + error); 
	}
}
