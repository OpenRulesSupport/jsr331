package javax.constraints.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Constraint;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.VarReal;
import javax.constraints.linear.LinearSolver;
import javax.constraints.linear.LinearSolverFactory;
import javax.constraints.linear.NonLinearProblemException;

import org.slf4j.LoggerFactory;

public class Problem extends AbstractProblem {
	
	static public final String JSR331_LINEAR_VERSION = "JSR-331 Common Implementation for Linear Solvers";
	
	int numberOfScalProducts;

	public Problem() {
		this("LinearProblem");
	}
	
	public Problem(String name) {
		super(name);
		numberOfScalProducts = 0;
	}
	
	@Override
	public Constraint postElement(int[] array, Var indexVar, String oper, int value) {
		throw new NonLinearProblemException("postElement(int[] coefficients, Var indexVar, String oper, int value)");
	}

	@Override
	public Constraint postElement(int[] array, Var indexVar, String oper, Var var) {
		throw new NonLinearProblemException("postElement(int[] coefficients, Var indexVar, String oper, Var var)");
	}

	@Override
	public Constraint postElement(Var[] vars, Var indexVar, String oper, int value) {
		throw new NonLinearProblemException("postElement(Var[] vars, Var indexVar, String oper, int value) ");
	}

	@Override
	public Constraint postElement(Var[] vars, Var indexVar, String oper, Var var) {
		throw new NonLinearProblemException("postElement(Var[] vars, Var indexVar, String oper, Var var)");
	}

	@Override
	public Constraint post(int[] coefficients, Var[] vars, String oper, int value) {
		if (coefficients.length != vars.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		javax.constraints.impl.Constraint constraint = new javax.constraints.impl.Constraint(this);
		constraint.setCoefficients(coefficients);
		constraint.setVars(vars);
		constraint.setOper(oper);
		constraint.setValue(value);
		add(constraint);
		return constraint;
	}

	@Override
	public Constraint post(int[] coefficients, Var[] vars, String oper, Var var) {
		if (coefficients.length != vars.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		Var[] vars1 = new Var[vars.length+1];
		int[] coefs = new int[vars.length+1];
		for (int i = 0; i < vars.length; i++) {
			vars1[i] = vars[i];
			coefs[i] = coefficients[i];
		}
		vars1[vars.length] = var;
		coefs[vars.length]= -1; 
		int value = 0;
		return post(coefs,vars1,oper,value);
	}
	
	@Override
	public Constraint post(Var[] vars, String oper, int value) {
		int[] coefs = new int[vars.length];
		for (int i = 0; i < vars.length; i++) {
			coefs[i] = 1;
		}
		return post(coefs,vars,oper,value); 
	}

	@Override
	public Constraint post(Var[] vars, String oper, Var var) {
		Var[] vars1 = new Var[vars.length+1];
		int[] coefs = new int[vars.length+1];
		for (int i = 0; i < vars.length; i++) {
			vars1[i] = vars[i];
			coefs[i] = 1;
		}
		vars1[vars.length] = var;
		coefs[vars.length]= -1; 
		int value = 0;
		return post(coefs,vars1,oper,value);
	}

	@Override
	public Constraint post(Var var, String oper, int value) {
		Var[] vars = { var };
		return post(vars,oper,value);
	}

	@Override
	public Constraint post(Var var1, String oper, Var var2) {
		Var[] vars = { var1, var2 };
		int[] coefficients = { 1, -1 };
		int value = 0;
		return post(coefficients, vars, oper, value);
	}

	@Override
	public Constraint linear(Var var, String oper, int value) {
		//throw new NonLinearProblemException("linear(Var var, String oper, int value)");
		javax.constraints.impl.Constraint constraint = new javax.constraints.impl.Constraint(this);
		constraint.setCoefficients(new int[]{1});
		constraint.setVars(new Var[]{var});
		constraint.setOper(oper);
		constraint.setValue(value);
		return constraint;
	}

	@Override
	public Constraint linear(Var var1, String oper, Var var2) {
		//throw new NonLinearProblemException("linear(Var var1, String oper, Var var2)");
		javax.constraints.impl.Constraint constraint = new javax.constraints.impl.Constraint(this);
		constraint.setCoefficients(new int[]{1,-1});
		constraint.setVars(new Var[]{var1,var2});
		constraint.setOper(oper);
		constraint.setValue(0);
		return constraint;
	}
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' value
	 * for an "array: of real coefficients to be multiplied by an array "vars"
	 * of integer constrained variables 
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(double[] coefficients, Var[] vars, String oper, double value) {
		return post(coefficients, (ConstrainedVariable[])vars, oper, value);
	}
	
	@Override
	public Constraint post(double[] coefficients, VarReal[] vars, String oper, double value) {
		return post(coefficients, (ConstrainedVariable[])vars, oper, value);
	}
	
	@Override
	public Constraint post(double[] coefficients, ConstrainedVariable[] vars, String oper, double value) {
		if (coefficients.length != vars.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		javax.constraints.impl.Constraint constraint = new javax.constraints.impl.Constraint(this);
		constraint.setCoefficients(coefficients); 
		constraint.setVars(vars);
		constraint.setOper(oper);
		constraint.setValue(value);
		add(constraint);
		return constraint;
	}

	@Override
	public Constraint post(double[] coefficients, VarReal[] vars, String oper, VarReal var) {
//		if (coefficients.length != vars.length)
//			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
//		VarReal[] vars1 = new VarReal[vars.length+1];
//		double[] coefs = new double[vars.length+1];
//		for (int i = 0; i < vars.length; i++) {
//			vars1[i] = vars[i];
//			coefs[i] = coefficients[i];
//		}
//		vars1[vars.length] = var;
//		coefs[vars.length]= -1; 
//		int value = 0;
//		return post(coefs,vars1,oper,value);
		return post(coefficients, (ConstrainedVariable[])vars, oper, (ConstrainedVariable)var);
	}

	public Constraint post(double[] coefficients, ConstrainedVariable[] vars, String oper, ConstrainedVariable var) {
		if (coefficients.length != vars.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		ConstrainedVariable[] vars1 = new ConstrainedVariable[vars.length+1];
		double[] coefs = new double[vars.length+1];
		for (int i = 0; i < vars.length; i++) {
			vars1[i] = vars[i];
			coefs[i] = coefficients[i];
		}
		vars1[vars.length] = var;
		coefs[vars.length]= -1; 
		int value = 0;
		return post(coefs,vars1,oper,value);
	}
	
//	@Override
	public Constraint post(VarReal[] vars, String oper, double value) {
		double[] coefs = new double[vars.length];
		for (int i = 0; i < vars.length; i++) {
			coefs[i] = 1;
		}
		return post(coefs,vars,oper,value); 
	}	
	

//	@Override
	public Constraint post(VarReal[] vars, String oper, VarReal var) {
		VarReal[] vars1 = new VarReal[vars.length+1];
		double[] coefs = new double[vars.length+1];
		for (int i = 0; i < vars.length; i++) {
			vars1[i] = vars[i];
			coefs[i] = 1;
		}
		vars1[vars.length] = var;
		coefs[vars.length]= -1; 
		double value = 0;
		return post(coefs,vars1,oper,value);
	}
	
//	@Override
	public Constraint post(VarReal var, String oper, double value) {
		VarReal[] vars = { var };
		return post(vars,oper,value);
	}

	@Override
	public Constraint post(VarReal var1, String oper, VarReal var2) {
		VarReal[] vars = { var1, var2 };
		double[] coefficients = { 1, -1 };
		double value = 0;
		return post(coefficients, vars, oper, value);
	}

	@Override
	public Constraint post(VarReal var1, String oper, Var var2) {
		return post(var1,oper,var2.asReal());
	}

	@Override
	public Constraint post(Var var1, String oper, VarReal var2) {
		return post(var1.asReal(),oper,var2);
	}
	
	/**
	 * Creates and posts a constraint: var "oper" scalProd(arrayOfValues,arrayOfVariables)
	 * @param var the constraint variable
	 * @param oper an operator
	 * @param coefficients the array of values
	 * @param vars the array of variables
	 * @return a constraint 
	 */
	public Constraint postScalProd(Var var, String oper, int[] coefficients, Var[] vars) {
		if (coefficients.length != vars.length)
			throw new RuntimeException("postScalProd: arrays of coefficients and vars should have the same length");
		
		int[] coefPlus1 = new int[coefficients.length+1];
		Var[] varsPlus1 = new Var[vars.length+1];
		for (int i = 0; i < vars.length; i++) {
			coefPlus1[i] = coefficients[i];
			varsPlus1[i] = vars[i];
		}
		coefPlus1[coefficients.length] = -1;
		varsPlus1[vars.length] = var;
		javax.constraints.impl.Constraint c = 
				(javax.constraints.impl.Constraint)post(coefPlus1,varsPlus1,oper,0);
		c.setDerivedVar(var);
		return c;
	}

	@Override
	public Var scalProd(int[] coefficients, Var[] vars) {
		if (coefficients.length != vars.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		
		int positiveMin = 0;
		int positiveMax = 0;
		int maxPositiveValue = 0;
		boolean areNegatives = false;
		for (int i = 0; i < vars.length; i++) {
			if (coefficients[i] < 0)
				areNegatives = true;
			if (vars[i].getMin() < 0)
				areNegatives = true;
			int prod = Math.abs(coefficients[i]*vars[i].getMax());
			if (maxPositiveValue < prod)
				maxPositiveValue = prod;
			positiveMin += vars[i].getMin()*coefficients[i];
			positiveMax += vars[i].getMax()*coefficients[i];
		}
		
		int min = positiveMin;
		int max = positiveMax;
		if (areNegatives == true) {
			max = maxPositiveValue*vars.length;
			min = -max;
		}
		//Var derivedVar = createVariable("_ScalProd"+(n+1)); //, min, max);
//		int min = Integer.MIN_VALUE+1;
//		int max = Integer.MAX_VALUE-1;
		int n = getConstraints().length;
		numberOfScalProducts++;
		Var derivedVar = createVariable("_ScalProd"+numberOfScalProducts, min, max);
		postScalProd(derivedVar, "=", coefficients, vars);
		remove("_ScalProd");
		// This variable needs to be added to the problem manually to avoid an uncompleted mps-file
		// add(derivedVar);
		return derivedVar;
	}
	
	@Override
	public Var scalProd(String name, int[] coefficients, Var[] vars) {
		if (coefficients.length != vars.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		
		int positiveMin = 0;
		int positiveMax = 0;
		int maxPositiveValue = 0;
		boolean areNegatives = false;
		for (int i = 0; i < vars.length; i++) {
			if (coefficients[i] < 0)
				areNegatives = true;
			if (vars[i].getMin() < 0)
				areNegatives = true;
			int prod = Math.abs(coefficients[i]*vars[i].getMax());
			if (maxPositiveValue < prod)
				maxPositiveValue = prod;
			positiveMin += vars[i].getMin()*coefficients[i];
			positiveMax += vars[i].getMax()*coefficients[i];
		}
		
		int min = positiveMin;
		int max = positiveMax;
		if (areNegatives == true) {
			max = maxPositiveValue*vars.length;
			min = -max;
		}
		int n = getConstraints().length;
		Var derivedVar = createVariable(name, min, max);
		add(name,derivedVar);
		postScalProd(derivedVar, "=", coefficients, vars);
		return derivedVar;
	}

	
	/**
	 * Returns a real constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 */
	@Override
	public VarReal scalProd(double[] arrayOfValues, VarReal[] arrayOfVariables) {
		if (arrayOfValues.length != arrayOfVariables.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		
		double positiveMin = 0;
		double positiveMax = 0;
		double maxPositiveValue = 0;
		boolean areNegatives = false;
		double[] coefPlus1 = new double[arrayOfValues.length+1];
		VarReal[] varsPlus1 = new VarReal[arrayOfVariables.length+1];
		for (int i = 0; i < arrayOfVariables.length; i++) {
			coefPlus1[i] = arrayOfValues[i];
			varsPlus1[i] = arrayOfVariables[i];
			
			if (arrayOfValues[i] < 0)
				areNegatives = true;
			if (arrayOfVariables[i].getMin() < 0)
				areNegatives = true;
			double prod = Math.abs(arrayOfValues[i]*arrayOfVariables[i].getMax());
			if (maxPositiveValue < prod)
				maxPositiveValue = prod;
			positiveMin += arrayOfVariables[i].getMin()*arrayOfValues[i];
			positiveMax += arrayOfVariables[i].getMax()*arrayOfValues[i];
		}
		
		double min = positiveMin;
		double max = positiveMax;
		if (areNegatives == true) {
			max = maxPositiveValue*arrayOfVariables.length;
			min = -max;
		}
		
		for (int i = 0; i < arrayOfVariables.length; i++) {
			coefPlus1[i] = arrayOfValues[i];
			varsPlus1[i] = arrayOfVariables[i];
		}
		coefPlus1[arrayOfValues.length] = -1;
		int n = getConstraints().length;
		numberOfScalProducts++;
		VarReal derivedVar = createVariableReal("_ScalProd"+numberOfScalProducts,min,max);
		varsPlus1[arrayOfVariables.length] = derivedVar;
		javax.constraints.impl.Constraint c = 
				(javax.constraints.impl.Constraint)post(coefPlus1,varsPlus1,"=",0);
		c.setDerivedVar((AbstractConstrainedVariable)derivedVar);
		return derivedVar;
	}
	
	public VarReal scalProd(double[] arrayOfValues, ConstrainedVariable[] arrayOfVariables) {
		if (arrayOfValues.length != arrayOfVariables.length)
			throw new RuntimeException("Arrays of coefficients and vars should have the same length");
		
		double[] coefPlus1 = new double[arrayOfValues.length+1];
		ConstrainedVariable[] varsPlus1 = new ConstrainedVariable[arrayOfVariables.length+1];
		for (int i = 0; i < arrayOfVariables.length; i++) {
			//ConstrainedVariable var = arrayOfVariables[i];
			coefPlus1[i] = arrayOfValues[i];
			varsPlus1[i] = arrayOfVariables[i];
		}
		coefPlus1[arrayOfValues.length] = -1;
		int n = getConstraints().length;
		numberOfScalProducts++;
		VarReal derivedVar = createVariableReal("_ScalProd"+numberOfScalProducts);
		varsPlus1[arrayOfVariables.length] = derivedVar;
		javax.constraints.impl.Constraint c = 
				(javax.constraints.impl.Constraint)post(coefPlus1,varsPlus1,"=",0);
		c.setDerivedVar((AbstractConstrainedVariable)derivedVar);
		return derivedVar;
	}
	
	@Override
	public Var sum(Var[] vars) {
		if (vars.length==0) {
			log("Attempt to find a sum of an empty array");
			return variable(0,0);
		}
		int min = 0;
		int max = 0;
		for (int i = 0; i < vars.length; i++) {
			min += vars[i].getMin();
			max += vars[i].getMax();
		}
		AbstractProblem p = (AbstractProblem) vars[0].getProblem();
		Var sumVar = p.variable("_sum_", min, max);
		p.post(vars, "=", sumVar);
		int[] coefficients = new int[vars.length];
		for (int i = 0; i < coefficients.length; i++) {
			coefficients[i] = 1;
		}
		postScalProd(sumVar,"=",coefficients,vars);
		p.remove("_sum_");
		return sumVar;
	}
	
	@Override
	public Var sum(String name,Var[] vars) {
		if (vars.length==0) {
			log("Attempt to find a sum of an empty array");
			return variable(0,0);
		}
		int min = 0;
		int max = 0;
		for (int i = 0; i < vars.length; i++) {
			min += vars[i].getMin();
			max += vars[i].getMax();
		}
		AbstractProblem p = (AbstractProblem) vars[0].getProblem();
		Var sumVar = p.variable(name, min, max);
//		p.post(vars, "=", sumVar);
		int[] coefficients = new int[vars.length];
		for (int i = 0; i < coefficients.length; i++) {
			coefficients[i] = 1;
		}
		postScalProd(sumVar,"=",coefficients,vars);
		return sumVar;
	}
	
	@Override
	public VarReal sum(VarReal[] vars) {
		double[] coefficients = new double[vars.length];
		for (int i = 0; i < coefficients.length; i++) {
			coefficients[i] = 1.0;
		}
		return scalProd(coefficients,vars);
	}
		
	public boolean isDerivedVar(AbstractConstrainedVariable var) {
		Constraint[] constraints = getConstraints();
		for (int i = 0; i < constraints.length; i++) {
			javax.constraints.impl.Constraint c = 
					(javax.constraints.impl.Constraint) constraints[i];
			if (var == c.getDerivedVar())
				return true;
		}
		return false;
	}
	
//	public boolean isDerivedVar(VarReal var) {
//		Constraint[] constraints = getConstraints();
//		for (int i = 0; i < constraints.length; i++) {
//			javax.constraints.impl.Constraint c = 
//					(javax.constraints.impl.Constraint) constraints[i];
//			if (var == c.getDerivedVar())
//				return true;
//		}
//		return false;
//	}
	
	public javax.constraints.impl.Constraint[] constraintsWithDerivedVars() {
		Vector<javax.constraints.impl.Constraint> vector = 
				new Vector<javax.constraints.impl.Constraint>();
		Constraint[] constraints = getConstraints();
		for (int i = 0; i < constraints.length; i++) {
			javax.constraints.impl.Constraint c = 
					(javax.constraints.impl.Constraint) constraints[i];
			if (c.getDerivedVar() != null)
				vector.add(c);
		}
		if (vector.isEmpty())
			return null;
		javax.constraints.impl.Constraint[] result = 
				new javax.constraints.impl.Constraint[vector.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = vector.get(i);
		}
		return result;		
	}
	
	public javax.constraints.impl.Constraint findConstraintForVariable(String name) {
		Constraint[] constraints = getConstraints();
		for (int i = 0; i < constraints.length; i++) {
			javax.constraints.impl.Constraint c = 
					(javax.constraints.impl.Constraint) constraints[i];
			if (c.getDerivedVar() != null && name.equals(c.getDerivedVar().getName()))
				return c;
		}
		return null;		
	}

	@Override
	public Constraint allDiff(Var[] vars) {
		throw new NonLinearProblemException("allDiff(Var[] vars)");
	}

	@Override
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, int value) {
		throw new NonLinearProblemException("postCardinality(Var[] vars, int cardValue, String oper, int value)");
	}

	@Override
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, Var var) {
		throw new NonLinearProblemException("postCardinality(Var[] vars, int cardValue, String oper, Var var)");
	}

	@Override
	public String getImplVersion() {
		return JSR331_LINEAR_VERSION;
	}

	@Override
	public Var createVariable(String name, int min, int max) {
		javax.constraints.impl.Var var = new javax.constraints.impl.Var(this,name);
		var.setMin(min);
		var.setMax(max);
		return var;
	}
	
	public Var createVariable(String name) {
		javax.constraints.impl.Var var = new javax.constraints.impl.Var(this,name);
		return var;
	}
	
	public VarReal createVariableReal(String name, double min, double max) {
		javax.constraints.impl.VarReal var = new javax.constraints.impl.VarReal(this,name);
		var.setMin(min);
		var.setMax(max);
		return var;
	}
	
	public VarReal createVariableReal(String name) {
		javax.constraints.impl.VarReal var = new javax.constraints.impl.VarReal(this,name);
		return var;
	}

	@Override
	public VarBool variableBool(String name) {
		return (VarBool)variable(name,0,1);
	}

	@Override
	public void post(Constraint constraint) {
		//throw new NonLinearProblemException("post(Constraint constraint) is not used for a linear problem");
		add(constraint);
	}

	@Override
	protected Solver createSolver() {
		LinearSolver solver = LinearSolverFactory.newLinearSolver(this);
		log("Solve problem using " + solver.getVersion());
		return solver;
	}

	private static org.slf4j.Logger logger = LoggerFactory.getLogger("javax.constraints");
	
	@Override
	public void log(String text) {
		logger.info(text);
	}

	@Override
	public void debug(String text) {
		logger.debug(text);
	}

	@Override
	public void error(String text) {
		logger.error(text);
	}
	
	@Override
	public void loadFromXML(InputStream in) throws Exception {
		throw new RuntimeException("loadFromXML is not implemented");
	}

	@Override
	public void storeToXML(OutputStream os, String comment) throws Exception {
		throw new RuntimeException("storeToXML is not implemented");
	}
	
//	public void add(AbstractConstrainedVariable var) {
//		add(var);
//	}

}
