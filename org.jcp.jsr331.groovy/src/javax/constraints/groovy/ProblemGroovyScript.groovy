package javax.constraints.groovy

import javax.constraints.Solution;
import javax.constraints.Var
import javax.constraints.Constraint
import javax.constraints.Solver

abstract class ProblemGroovyScript extends Script {
	static problem = new javax.constraints.impl.Problem("GroovyCSP")
	
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
		
	}
	
	def Var variable(String name,int min, int max) {
		return problem.variable(name,min,max);
	}
	
	def Var variable(String name, groovy.lang.IntRange range) {
		int[] array = new int[range.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = range.get(i).intValue()
		}
		return problem.variable(name,array)
	}
	
	def Var variable(String name, java.util.ArrayList<Integer> range) {
		int[] array = new int[range.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = range.get(i).intValue()
		}
		return problem.variable(name,array)
	}
	
	def Constraint post(Var var, String oper, int value) {
		return problem.post(var,oper,value)
	}
	
	def Constraint post(Var var1, String oper, Var var2) {
		return problem.post(var1,oper,var2)
	}
	
	def Constraint postAllDiff(Var[] vars) {
		return problem.postAllDiff(vars)
	}
	
	def Constraint postAllDifferent(java.util.ArrayList<Var> vars) {
		return problem.postAllDiff(vars)
	}
	
	def Constraint postAllDifferent(java.util.List vars) {
		return problem.postAllDiff(vars)
	}
	
	def Solver getSolver() {
		return problem.solver
	}
	
	def Solution findSolution() {
		return getSolver().findSolution()
	}
	
	def void log(String text) {
		problem.log(text)
	}
	
	 
	
}

//import org.codehaus.groovy.control.*
//
//if(args) {
//	def conf = new CompilerConfiguration()
//	conf.setScriptBaseClass("ProblemGroovyScript")
//	def shell = new GroovyShell(conf)
//	shell.evaluate(new File(args[0]))
//}
//else
//	println "Usage: ProblemGroovy <script>"
