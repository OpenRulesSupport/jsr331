package hello;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetProvider;

public class RunHelloJsr94Multi {

	public void run() throws Exception {

		// register RuleServiceProvider
		String ruleServiceProviderUri = "com.openrules.jsr94.rules.RuleServiceProviderImpl";
		Class RuleServiceProviderClass = com.openrules.jsr94.rules.RuleServiceProviderImpl.class;
		RuleServiceProviderManager.registerRuleServiceProvider(
				ruleServiceProviderUri, RuleServiceProviderClass);

		// get RuleServiceProvider from RuleServiceProviderManager
		RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager
				.getRuleServiceProvider(ruleServiceProviderUri);

		// get RuleAdministrator from RuleServiceProvider
		RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();

		// get RuleExecutionSetProvider from RuleAdministrator
		Map ruleExecutionSetProviderProperties = new HashMap();
		RuleExecutionSetProvider ruleSetProvider = ruleAdministrator
				.getRuleExecutionSetProvider(ruleExecutionSetProviderProperties);

		// create RuleExecutionSet from ruleExecutionSetFileUri
		String ruleExecutionSetFileUri = "file:rules/HelloCustomer.xls";
		Map ruleExecutionSetCreationProperties = new HashMap();
		RuleExecutionSet ruleExecutionSet = ruleSetProvider
				.createRuleExecutionSet(ruleExecutionSetFileUri,ruleExecutionSetCreationProperties);

		// register/bind the RuleExecutionSet as ruleExecutionSetBindUri
		String ruleExecutionSetBindUri = "HelloCustomer";
		Map ruleExecutionSetRegistrationProperties = new HashMap();
		ruleAdministrator.registerRuleExecutionSet(ruleExecutionSetBindUri,
				ruleExecutionSet, ruleExecutionSetRegistrationProperties);

		// get RuleRuntime from RuleServiceProvider
		RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime();

		// create StatelessRuleSession in RuleRuntime
		Map ruleSessionProperties = new HashMap();
		StatelessRuleSession statelessRuleSession = (StatelessRuleSession) ruleRuntime
				.createRuleSession(ruleExecutionSetBindUri,
						ruleSessionProperties,
						RuleRuntime.STATELESS_SESSION_TYPE);

		// execute rules "helloSummer"
		Customer customer = new Customer();
		customer.setName("Robinson");
		customer.setGender("Female");
		customer.setMaritalStatus("Married");

		Response response = new Response();

		Object[] args = { customer, response };
		
		ruleSessionProperties.put("method", "helloSummer");
		List result = statelessRuleSession.executeRules(Arrays.asList(args));

		System.out.println("Summer Response: " + response.getMap().get("greeting")
				+ ", " + response.getMap().get("salutation")
				+ customer.getName() + "!");
		
		// execute rules "helloWinter"
		ruleSessionProperties.put("method", "helloWinter");
		statelessRuleSession.executeRules(Arrays.asList(args));
		System.out.println("Winter Response: " + response.getMap().get("greeting")
				+ ", " + response.getMap().get("salutation")
				+ customer.getName() + "!");

	}

	static public void main(String[] args) throws Exception {
		new RunHelloJsr94Multi().run();
	}

}