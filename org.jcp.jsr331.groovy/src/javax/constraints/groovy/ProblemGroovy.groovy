package javax.constraints.groovy

import javax.constraints.Var;

class ProblemGroovy extends javax.constraints.impl.Problem {
	
	public ProblemGroovy() {
		super();
	}
	public ProblemGroovy(String name) {
		super(name);
	}
	
	static {
		Integer.metaClass.multiply = { Var var -> 
			var*delegate
		}
		
		Integer.metaClass.plus = { Var var ->
			var+delegate
		}
		
		Integer.metaClass.minus = { Var var ->
			delegate+var.negative()
		}
		
		Integer.metaClass.divide = { Var var ->
			Var v1 = new javax.constraints.impl.Var(var.getProblem(),"temp",1,1);
			delegate*(v1.divide(var))
		}
		
		
//		Var.metaClass.compareTo = { Var var ->
//			var.getProblem().linear(delegate, "<", var)
//		}
		
//		Var.metaClass.compareTo = { int value ->
//			Var thisVar = (Var) delegate
//			int val = value
//			var.getProblem().linear(var, "<", val)
//		}
	}
	
	public Var variable(String name, groovy.lang.IntRange range) {
		int[] array = new int[range.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = range.get(i).intValue();
		}
		return variable(name,array);
	}
	
	public Var variable(String name, java.util.ArrayList<Integer> range) {
		int[] array = new int[range.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = range.get(i).intValue();
		}
		return variable(name,array);
	}
	
	def static operators = ["<=", ">=", "==", "<", ">", "!=", "=" ] 
	
	public void post(String exp) {
		def shell = new GroovyShell()
		for(oper in operators) {
			//	operators.each { oper ->
			def ind = exp.indexOf(oper)
			if (ind >= 0) {
				try {
					def elements = exp.split(oper);
					println oper
					String leftString = elements[0].trim()
					println leftString
					def left = shell.evaluate(leftString)
					String rightString = elements[2].trim()
					println rightString
					def right = shell.evaluate(rightString);
					post(left, oper, right);
					return;
				}
				catch (Exception e) {
					throw new RuntimeException ("Invalid expression: " + exp);
				}
			}
		}
		throw new RuntimeException ("Invalid expression: " + exp);
	}

	static main(args) {
		int num = 10;
		ProblemGroovy p = new ProblemGroovy()
		Var x = p.variable("x",1,5)
		p.post(x,"<",3)
		println "IntMultiply: " + 10.multiply(x)

		println "Int*: " + 10*x

		Var mul = 5*x
		p.log(mul)
		
		try {
			Var y = p.variable("y",1,3)
			def constraint = x.compareTo(y)
			//def constraint = x<y
			p.log("constraint: " + constraint)
			p.log(x)
		}
		catch(Exception e) {
			println "fail fo post(x<3)"
			e.printStackTrace()
		}
		
		
		
//		p.post("mul*mul < 25");
//		p.log(mul);
	
	}

}
