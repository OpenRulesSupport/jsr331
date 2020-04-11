package javax.constraints.impl;

import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import JSetL.IntLVar;
import JSetL.MultiInterval;

/**
 * This class implement the JSR331 constrained integer variable "Var",
 * extending the common implementation AbstractVar. The implementation is
 * based on the solver JSetL.
 * 
 * @author Fabio Biselli
 */
public class Var extends AbstractVar  {
	
	/**
	 * Builds an unbound integer variable with an auto-generated name.
	 * 
	 * @param problem the problem which the variable is related.
	 */
	public Var(Problem problem) {
		super(problem);
		String name = problem.getFreshName();
		setImpl(new IntLVar(name));
		setName(name);
	}
	
	/**
	 * Builds an unbound integer variable with an auto-generated name.
	 * 
	 * @param problem the problem which the variable is related,
	 * @param min the integer representing the lower bound,
	 * @param max the integer representing the upper bound.
	 * 
	 */
	public Var(Problem problem, int min, int max) {
		super(problem);
		String name = problem.getFreshName();
		setImpl(new IntLVar(name, min, max));
		setName(name);
	}
	
	/**
	 * Builds a bound or unbound integer variable from an IntLVar variable.
	 * The built variable is bound if and only if the parameter 
	 * <code>x</code> of
	 * type IntLVar is bound.
	 * 
	 * @param problem the problem which the variable is related.
	 * @param x the JSetL integer variable from which build the JSR331 
	 * variable.
	 *  
	 */	
	public Var(Problem problem, IntLVar x) {
		super(problem, x.getName());
		setImpl(x);
	}
	/**
	 * Copy constructor.
	 * 
	 * @param x the JSR331 variable to copy.
	 */
	public Var(Var x) {
		super(x.getProblem(), x.getName());
		setImpl(new IntLVar(x.getIntLVar()));
	}

	/**
	 * Build an unbound integer variable named <code> name </code>.
	 * 
	 * @param problem the problem which the variable is related.
	 * @param name the name of the JSR331 variable built.
	 */
	public Var(Problem problem, String name) {
		super(problem, name);
		setImpl(new IntLVar(name));
	}

	/**
	 * Returns the minimum value of the variable domains. 
	 */
	public int getMin() {
		MultiInterval domain = ((IntLVar) getImpl()).getDomain();
		return domain.getGlb();
	}

	/**
	 * Returns the maximum value of the variable domains. 
	 */
	public int getMax() {
		MultiInterval domain = ((IntLVar) getImpl()).getDomain();
		return domain.getLub();
	}

	/**
	 * Returns a boolean value. True if and only if the variable contains 
	 * only one values, false otherwise. In other words return true if the 
	 * relative domain is
	 * of the form <code>[a;a]</code>.  
	 */
	public boolean isBound() {
		return ((IntLVar) getImpl()).isBound();
	}

	/**
	 * Returns a boolean value. True if and only if the domain of the variable
	 * contains the integer <code>value</code>.
	 */
	public boolean contains(int value) {
		MultiInterval domain = ((IntLVar) getImpl()).getDomain();
		return domain.contains(value);
	}

	/**
	 * Returns a new Var that is constrained to be the sum of 
	 * <code>this</code>
	 * and the integer value <code>value</code>.  
	 */
	public Var plus(int value) {
		Problem p = (Problem) getProblem();
		Var x = new Var(p, p.getFreshName());
		x.setImpl(((IntLVar) getImpl()).sum(value));
		((IntLVar)x.getImpl()).setName(x.getName());
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) x.getImpl()).eq(((IntLVar) getImpl()).sum(value)));
		p.post(c);
		p.addAuxVariable(x);
		return x;
	}
	
	/**
	 * Returns a new Var that is constrained to be the difference of 
	 * <code>this</code>
	 * and the integer value <code>value</code>.  
	 */
	public Var minus(int value) {
		Problem p = (Problem) getProblem();
		Var x = new Var(p, p.getFreshName());
		x.setImpl(((IntLVar) getImpl()).sub(value));
		((IntLVar)x.getImpl()).setName(x.getName());
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) x.getImpl()).eq(((IntLVar) getImpl()).sub(value)));
		p.post(c);
		p.addAuxVariable(x);
		return x;
	}


	/**
	 * Returns a new Var that is constrained to be the sum of 
	 * <code>this</code> and the variable <code>x</code>.  
	 */
	public Var plus(javax.constraints.Var x) {
		Problem p = (Problem) getProblem();
		Var a = new Var(p, p.getFreshName());
		a.setImpl(((IntLVar) getImpl()).sum(((Var) x).getIntLVar()));
		((IntLVar)a.getImpl()).setName(a.getName());	
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) a.getImpl()).eq(
						((IntLVar) getImpl()).sum((IntLVar) x.getImpl())));
		p.post(c);
		p.addAuxVariable(a);
		return a;
	}

	/**
	 * Returns a new Var that is constrained to be the difference of 
	 * <code>this</code>
	 * and the variable <code>x</code>.  
	 */
	public Var minus(javax.constraints.Var x) {
		Problem p = (Problem) getProblem();
		Var a = new Var(p, p.getFreshName());
		a.setImpl(((IntLVar) getImpl()).sub(((Var) x).getIntLVar()));
		((IntLVar)a.getImpl()).setName(a.getName());
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) a.getImpl()).eq(
						((IntLVar) getImpl()).sub((IntLVar) x.getImpl())));
		p.post(c);
		p.addAuxVariable(a);
		return a;
	}
	
	/**
	 * Returns a new Var that is constrained to be the multiple of 
	 * <code>this</code>
	 * and the integer value <code>value</code>.  
	 */
	public Var multiply(int value) {
		Problem p = (Problem) getProblem();
		Var x = new Var(p, p.getFreshName());
		x.setImpl(((IntLVar) getImpl()).mul(value));
		((IntLVar)x.getImpl()).setName(x.getName());
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) x.getImpl()).eq(((IntLVar) getImpl()).mul(value)));
		p.post(c);
		p.addAuxVariable(x);
		return x;
	}

	/**
	 * Returns a new Var that is constrained to be the multiple of 
	 * <code>this</code>
	 * and the variable <code>x</code>.  
	 */
	public Var multiply(javax.constraints.Var x) {
		Problem p = (Problem) getProblem();
		Var a = new Var(p, p.getFreshName());
		a.setImpl(((IntLVar) getImpl()).mul(((Var) x).getIntLVar()));
		((IntLVar)a.getImpl()).setName(a.getName());
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) a.getImpl()).eq(
						((IntLVar) getImpl()).mul((IntLVar) x.getImpl())));
		p.post(c);
		p.addAuxVariable(a);
		return a;
	}
	
	/**
	 * Returns a new Var that is constrained to be the division of 
	 * <code>this</code>
	 * and the integer value <code>value</code>.  
	 */
	public Var divide(int value) {
		Problem p = (Problem) getProblem();
		Var x = new Var(p, p.getFreshName());
		x.setImpl(((IntLVar) getImpl()).div(value));
		((IntLVar)x.getImpl()).setName(x.getName());
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) x.getImpl()).eq(((IntLVar) getImpl()).div(value)));
		p.post(c);
		p.addAuxVariable(x);
		return x;
	}
	
	/**
	 * Returns a new Var that is constrained to be the division of 
	 * <code>this</code>
	 * and the variable <code>x</code>.  
	 */
	public Var divide(javax.constraints.Var x) {
		Problem p = (Problem) getProblem();
		Var a = new Var(p, p.getFreshName());
		a.setImpl(((IntLVar) getImpl()).div(((Var) x).getIntLVar()));
		((IntLVar)a.getImpl()).setName(a.getName());	
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) a.getImpl()).eq(
						((IntLVar) getImpl()).div((IntLVar) x.getImpl())));
		p.post(c);
		p.addAuxVariable(a);
		return a;
	}
	
	/**
	 * Returns a new Var that is constrained to be the reminder after 
	 * performing integer division between <code>this</code> and the given
	 * integer <code>value</code>.
	 */
	public Var mod(int value) {
		Problem p = (Problem) getProblem();
		Var x = new Var(p, p.getFreshName());
		x.setImpl(((IntLVar) getImpl()).mod(value));
		((IntLVar)x.getImpl()).setName(x.getName());
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) x.getImpl()).eq(((IntLVar) getImpl()).mod(value)));
		p.post(c);
		p.addAuxVariable(x);
		return x;
	}

	/**
	 * Returns a new Var that is constrained to be the absolute value 
	 * of <code>this</code>.
	 */
	public Var abs() {
		Problem p = (Problem) getProblem();
		IntLVar x = ((IntLVar) this.getImpl()).abs();
		x.setName(p.getFreshName());
		Var result = new Var(p, x);
		// To constraint the new variable.
		Constraint c = new Constraint(p,
				((IntLVar) result.getImpl()).eq(((IntLVar) getImpl()).abs()));
		p.post(c);
		p.addAuxVariable(x);
		return result;
		
	}

	@Override
	public void addPropagator(Propagator propagator, PropagationEvent event) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Getter method for the field <code>impl</code> of type IntLVar in 
	 * CommonBase.
	 * 
	 * @return the JSetL integer variable.
	 */
	public IntLVar getIntLVar() {
		return (IntLVar) getImpl();
	}	
	
}
