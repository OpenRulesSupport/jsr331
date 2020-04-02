package javax.constraints.impl;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.impl.AbstractConstrainedVariable;
import javax.constraints.impl.AbstractConstraint;

/**
 * This is a placeholder for linear constraints that look like:
 * 
 * coefficients * vars 'oper; value
 * 
 * If coefficients == null then it is simply
 * 
 * sum of vars 'oper' value
 * 
 * Scalar products define the following constraint:
 * 
 * {coefficients,-1} * {vars,derivedVar} = 0
 * 
 * 
 */

public class Constraint extends AbstractConstraint {
	
	String oper;
	double[] coefficients;
	
	ConstrainedVariable[] vars;
	double value;
	
	ConstrainedVariable derivedVar;

	public Constraint(Problem problem) {
		super(problem);
		coefficients = null;
		vars = null;
		derivedVar = null;
		int count = problem.getConstraints().length;
		String name = "C" + (count+1); // + " " + oper + " " + value;
		setName(name);
	}

	public double[] getCoefficients() {
		return coefficients;
	}

	public void setCoefficients(int[] coefficients) {
		double[] darray = new double[coefficients.length];
		for (int i = 0; i < darray.length; i++) {
			darray[i] = (double)coefficients[i];
		}
		this.coefficients = darray;
	}
	
	public void setCoefficients(double[] coefficients) {
		this.coefficients = coefficients;
	}
	
	public void negateCoefficients() {
		for (int i = 0; i < coefficients.length; i++) {
			coefficients[i] = -coefficients[i];
		}
	}

	public ConstrainedVariable[] getVars() {
		return vars;
	}

	public void setVars(ConstrainedVariable[] vars) {
		this.vars = vars;
	}
	
	public void setVars(Var[] vars) {
		ConstrainedVariable[] acvars = new ConstrainedVariable[vars.length];
		for (int i = 0; i < acvars.length; i++) {
			acvars[i] = (AbstractConstrainedVariable)vars[i];
		}
		this.vars = acvars;
	}
	
	public void setVars(VarReal[] vars) {
		ConstrainedVariable[] acvars = new ConstrainedVariable[vars.length];
		for (int i = 0; i < acvars.length; i++) {
			acvars[i] = (AbstractConstrainedVariable)vars[i];
		}
		this.vars = acvars;
	}
	
	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public ConstrainedVariable getDerivedVar() {
		return derivedVar;
	}

	public void setDerivedVar(ConstrainedVariable derivedVar) {
		this.derivedVar = derivedVar;
	}
	
}
