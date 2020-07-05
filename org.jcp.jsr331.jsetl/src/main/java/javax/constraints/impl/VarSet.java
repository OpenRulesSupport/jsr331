package javax.constraints.impl;

import java.util.Set;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Var;
import javax.constraints.impl.search.Solver;

import jsetl.IntLVar;
import jsetl.MultiInterval;
import jsetl.SetLVar;



/**
 * This class implement the JSR331 constrained set variable "VarSet",
 * extending the common implementation. The implementation is
 * based on the solver jsetl. The domain of a constrained set variable 
 * is a set of Sets that consist of regular integers.
 * 
 * @author Fabio Biselli
 */
public class VarSet extends AbstractConstrainedVariable 
	implements javax.constraints.VarSet {

	/**
	 * Build a new unbound set variable.
	 * 
	 * @param problem the problem which the variable is related.
	 */
	public VarSet(javax.constraints.Problem problem) {
		super(problem);
		setImpl(new SetLVar());
		setName(((Problem)problem).getFreshName());
	}
	
	/**
	 * Build a new unbound set variable.
	 * 
	 * @param problem the problem which the variable is related.
	 * @param name a String that represent the variable name.
	 */
	public VarSet(javax.constraints.Problem problem, String name) {
		super(problem);
		setImpl(new SetLVar(name));
		setName(name);
	}
	
	/**
	 * Build a new set variable that contain the given array of
	 * integers.
	 * 
	 * @param problem the problem which the variable is related
	 * @param values the integers contained in the set
	 * @param name a String that represent the variable name.
	 */
	public VarSet(Problem problem,  int[] values, String name) {
		super(problem, name);
		MultiInterval s = new MultiInterval();
		for (int i = 0; i < values.length; i++)
			s.add(values[i]);
		setImpl(new SetLVar(name,s));
	}
	
	/**
	 * Build a new set variable with specified required and possible values.
	 * 
	 * @param problem the problem which the variable is related
	 * @param required a set of integer (the glb)
	 * @param possible a set of integer (the lub)
	 * @param name a String that represent the variable name.
	 */
	public VarSet(Problem problem, Set<Integer> required, 
			Set<Integer> possible, String name){
		super(problem, name);
		setImpl(new SetLVar(name, required, possible));
	}

	/**
	 * Build a new set variable with specified optional values.
	 * 
	 * @param problem the problem which the variable is related
	 * @param lb a MultiInterval that represent the optional values
	 * @param name a String that represent the variable name.
	 */
	public VarSet(Problem problem, MultiInterval lb, String name) {
		super(problem, name);
		setImpl(new SetLVar(name, lb, MultiInterval.universe()));
	}

	/**
	 * Build a new set variable from a given jsetl.SetLVar.
	 * 
	 * @param problem the problem which the variable is related
	 * @param s a SetLVar that represent the optional values.
	 */
	public VarSet(Problem problem, SetLVar s) {
		super(problem);
		setImpl(s);
		setName(s.getName());
	}

	/**
	 * Returns a boolean value.
	 * True when all elements in the domain are required.
	 */
	public boolean isBound() {
		return ((SetLVar) getImpl()).isBound();
	}

	/**
	 * Getter method for the required elements of the set variable.
	 * 
	 * @return a Set of integers in the required set.
	 * @throws Exception if the VarSet is not bound.
	 */
	public Set<Integer> getValue() throws Exception {
		if (!isBound())
			throw new RuntimeException("Variable Set not bound");
		SetLVar set = (SetLVar) getImpl();
		Set<Integer> result = set.getValue();
		return result;
	}

	/**
	 * This method instantiates <code>this</code> set variable with the 
	 * constant "set" of integers. If one or more elements of <code>set</code>
	 * is not possible (not in the GLB of SetInterval) a runtime exception
	 * is thrown. Otherwise a new SetInterval is build such as:
	 * 
	 * <code>set</code> is in GLB and <code>set</code> is in LUB.
	 * 
	 * Nothing else is in the LUB of the new SetInterval.
	 * 
	 * @param set a set of integer.
	 * @throws Exception 
	 */
	public void setValue(Set<Integer> set) throws Exception {
		((SetLVar) this.getImpl()).setValue(set);
	}

	/**
	 * Getter method for the required elements of the variable set.
	 * 
	 * @return a new Set of integers that is required in the variable set.
	 */
	public Set<Integer> getRequiredSet() {
		return ((SetLVar) getImpl()).getDomain().getGlb();
	}

	/**
	 * Getter method for the possible elements of the variable set.
	 * 
	 * @return a new Set of integers that is possible in the variable set.
	 */
	public Set<Integer> getPossibleSet() {
		return ((SetLVar) getImpl()).getDomain().getLub();
	}

	/**
	 * Returns a boolean value. True if the given int value <code>value</code>
	 * is in the possible values of the variable set.
	 */
	public boolean isPossible(int value) {
		return ((SetLVar) getImpl()).getDomain().getLub().contains(value);
	}

	/**
	 * Returns a boolean value. True if the given int value <code>value</code>
	 * is in the required values of the variable set.
	 */
	public boolean isRequired(int value) {
		return ((SetLVar) getImpl()).getDomain().getGlb().contains(value);
	}

	/**
	 * Remove the integer <code>value</code> from the possible values of
	 * the variable set.
	 */
	public void remove(int value) throws Exception {
		((SetLVar) getImpl()).getDomain().getLub().remove(value);	
	}

	/**
	 * Add the integer <code>value</code> to the required values of
	 * the variable set.
	 */
	public void require(int value) throws Exception {
		((SetLVar) getImpl()).getDomain().getGlb().add(value);	
	}

	/**
	 * Returns a boolean value. True if the possible set contains 
	 * <code>setOfValues</code>.
	 */
	public boolean contains(Set<Integer> setOfValues) {
		return ((SetLVar) getImpl()).getDomain().getLub().containsAll(
				setOfValues);
	}

	/**
	 * Build and returns a new set variable that is an intersection of 
	 * <code>this</code> set variable with the <code>varSet</code> passed as 
	 * a parameter.
	 */
	public VarSet intersection(javax.constraints.VarSet varSet) {//throws Exception {
		Problem p = (Problem) getProblem();
		SetLVar tmp = ((SetLVar) varSet.getImpl());
		VarSet result = new VarSet(p);
		result.setImpl(((SetLVar)this.getImpl()).intersect(tmp));
		// To constraint the new variable.
		Constraint c = new Constraint(p, ((SetLVar) result.getImpl()).eq(
				((SetLVar)getImpl()).intersect(tmp)));
		p.post(c);
		return result;
	}

	/**
	 * Sets the cardinality of this set variable to be equals 0 if the flag
	 *  is true, and to be more or equal 1 if the flag is false. 
	 */
	public void setEmpty(boolean flag) {
		IntLVar cardinality = ((SetLVar) getImpl()).card();
		Solver solver = (Solver) getProblem().getSolver();
		if (flag == true)
			solver.addJSetLConstraint(cardinality.eq(0));
		else solver.addJSetLConstraint(cardinality.ge(1));
	}

	/**
	 * Build and returns a new set variable that is the union of 
	 * <code>this</code> set variable with the <code>varSet</code> passed as 
	 * a parameter.
	 */
	public VarSet union(javax.constraints.VarSet varSet) throws Exception {
		Problem p = (Problem) getProblem();
		SetLVar tmp = ((SetLVar) varSet.getImpl());
		VarSet result = new VarSet(p);
		result.setImpl(((SetLVar)getImpl()).union(tmp));
		// To constraint the new variable.
		Constraint c = new Constraint(p,
			((SetLVar) result.getImpl()).eq(((SetLVar)getImpl()).union(tmp)));
		p.post(c);
		return result;
	}

	/**
	 * Getter method for the set variable cardinality. Builds and returns a new
	 * Var bound to the cardinality of implementation variable set
	 * (FSVar).
	 * 
	 *  @return a new Var.
	 */
	public Var getCardinality() {
		Var result = new Var((Problem) getProblem(), 
				((SetLVar) getImpl()).card());
		return result;
	}
	
	/**
	 * Auxiliary output method. 
	 * 
	 * @return the output string.
	 */
	public String toString() {
		return ((SetLVar) getImpl()).toString();
	}
	
}
