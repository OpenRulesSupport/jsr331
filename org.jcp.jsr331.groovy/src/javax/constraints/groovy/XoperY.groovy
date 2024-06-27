package javax.constraints.groovy

/*
 * This methods splits an expression like "x*3+y*4-z*7 <= 16" in three parts:
 * parts[0]: x*3+y*4-z*7
 * parts[1]: <=
 * parts[2]: 16
 */
class XoperY {
	def static operators = ["<=", ">=", "==", "<", ">", "!=", "=" ]
	
	static String[] apply(String exp) {
		def results = ["?","?","?"]
		for(oper in operators) {
			//	operators.each { oper ->
			def ind = exp.indexOf(oper)
			if (ind >= 0) {
				try {
					def elements = exp.split(oper);
					results[0] = elements[0].trim();
					results[2] = elements[1].trim();
					results[1] = oper;
					return results;
				}
				catch (Exception e) {
					throw new RuntimeException ("Invalid expression: " + exp);
				}
			}
		}
		if (results[1].equals("?"))
			throw new RuntimeException ("Invalid expression: " + exp);
		return results
	}
	
	static main(args) {
		String exp = "x*3+y*4-z*7 != 16"
		println "Split ${exp} in three parts:\n" + XoperY.apply(exp);	
	}
}

