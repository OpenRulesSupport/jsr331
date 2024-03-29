package javax.constraints.impl.constraint;

import java.util.ArrayList;
import java.util.List;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.Solver;

import JSetL.IntLVar;
import JSetL.LList;
import JSetL.SolverClass;
import JSetL.lib.ListOps;

/**
 * Implements a global constraint that deal with elements of the arrays of 
 * constrained variables. If a constrained integer variable <code>index</code> 
 * serve as an index within an array <code>V</code>, than the result of 
 * <code>V[index]</code> will be another constrainted variable. 
 * 
 * <p>This was introduced since Java does not allow overloading ot the operator 
 * ``[]''. The class represents a linear constraint that involve the variable 
 * <code>V[index]</code> and an integer value or a variable.
 * 
 * @author Fabio Biselli
 *
 */
public class Element extends Constraint {

	/**
	 * Create a new Constraint such as: <code> array[indexVar]
	 * oper value </code>.
	 * 
	 * @param array an array of integers
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "array"
	 * @param oper an operator
	 * @param value the integer value.
	 * 
	 * @throws RuntimeException if the posting fails 
	 */
	public Element(
			int[] array, 
			javax.constraints.Var indexVar, 
			String oper,
			int value) {
		super(indexVar.getProblem());
		if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Problem p = (Problem) indexVar.getProblem();
		JSetL.Constraint element;
		IntLVar z = new IntLVar(p.getFreshName());
		IntLVar index = (IntLVar) indexVar.getImpl();
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		ListOps listOps = new ListOps(solver);
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++)
			list.add(i,array[i]);
		LList l = new LList("array", list);
		element = new JSetL.Constraint(listOps.ithElem(l, index, z));
		JSetL.Constraint linear = null;
        if (oper.equals("=")) {
        	linear = new JSetL.Constraint(element.and(z.eq(value)));
        }
        if (oper.equals("!=")) {
        	linear = new JSetL.Constraint(element.and(z.neq(value)));
        }
        if (oper.equals(">")) {
        	linear = new JSetL.Constraint(element.and(z.gt(value)));
        }
        if (oper.equals(">=")) {
        	linear = new JSetL.Constraint(element.and(z.ge(value)));
        }
        if (oper.equals("<")) {
        	linear = new JSetL.Constraint(element.and(z.lt(value)));
        }
        if (oper.equals("<=")) {
        	linear = new JSetL.Constraint(element.and(z.le(value)));
        }
        setImpl(linear);
        p.addAuxVariable(z);
        p.addAuxVariable(indexVar);
	}
	
	/**
	 * Create a new Constraint such as: <code> array[indexVar]
	 * oper value </code>.
	 * 
	 * @param array an array of integers
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "array"
	 * @param oper an operator
	 * @param var the integer variable.
	 * 
	 * @throws RuntimeException if the posting fails 
	 */
	public Element(
			int[] array, 
			javax.constraints.Var indexVar, 
			String oper,
			javax.constraints.Var var) {
		super(indexVar.getProblem());
		if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Problem p = (Problem) var.getProblem();
		JSetL.Constraint element;
		IntLVar z = new IntLVar(p.getFreshName());
		IntLVar index = (IntLVar) indexVar.getImpl();
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		ListOps listOps = new ListOps(solver);
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++)
			list.add(i,array[i]);
		LList l = new LList("array", list);
		element = new JSetL.Constraint(listOps.ithElem(l, index, z));
		JSetL.Constraint linear = null;
        if (oper.equals("=")) {
        	linear = new JSetL.Constraint(
        			element.and(z.eq((IntLVar)var.getImpl())));
        }
        if (oper.equals("!=")) {
        	linear = new JSetL.Constraint(
        			element.and(z.neq((IntLVar)var.getImpl())));
        }
        if (oper.equals(">")) {
        	linear = new JSetL.Constraint(
        			element.and(z.gt((IntLVar)var.getImpl())));
        }
        if (oper.equals(">=")) {
        	linear = new JSetL.Constraint(
        			element.and(z.ge((IntLVar)var.getImpl())));
        }
        if (oper.equals("<")) {
        	linear = new JSetL.Constraint(
        			element.and(z.lt((IntLVar)var.getImpl())));
        }
        if (oper.equals("<=")) {
        	linear = new JSetL.Constraint(
        			element.and(z.le((IntLVar)var.getImpl())));
        }
        setImpl(linear);
        p.addAuxVariable(z);
        p.addAuxVariable(indexVar);
        p.addAuxVariable(var);
	}
	
	/**
	 * Create a new Constraint such as: <code> vars[indexVar]
	 * oper value </code>.
	 * 
	 * @param vars an array of integer variables
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the array "vars"
	 * @param oper an operator
	 * @param value the integer value.
	 * 
	 * @throws RuntimeException if the posting fails 
	 */
	public Element(
			javax.constraints.Var[] vars, 
			javax.constraints.Var indexVar, 
			String oper, 
			int value) {
		super(indexVar.getProblem());
		if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Problem p = (Problem) indexVar.getProblem();
		JSetL.Constraint element;
		IntLVar z = new IntLVar(p.getFreshName());
		IntLVar index = (IntLVar) indexVar.getImpl();
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		ListOps listOps = new ListOps(solver);
		List<IntLVar> list = 
			new ArrayList<IntLVar>();
		for (int i = 0; i < vars.length; i++)
			list.add(i,(IntLVar) vars[i].getImpl());
		LList l = new LList("array", list);
		element = new JSetL.Constraint(listOps.ithElem(l, index, z));
		JSetL.Constraint linear = null;
		if (oper.equals("=")) {
			linear = new JSetL.Constraint(element.and(z.eq(value)));
        }
        if (oper.equals("!=")) {
        	linear = new JSetL.Constraint(element.and(z.neq(value)));
        }
        if (oper.equals(">")) {
        	linear = new JSetL.Constraint(element.and(z.gt(value)));
        }
        if (oper.equals(">=")) {
        	linear = new JSetL.Constraint(element.and(z.ge(value)));
        }
        if (oper.equals("<")) {
        	linear = new JSetL.Constraint(element.and(z.lt(value)));
        }
        if (oper.equals("<=")) {
        	linear = new JSetL.Constraint(element.and(z.le(value)));
        }
        setImpl(linear);
        p.addAuxVariable(z);
        p.addAuxVariable(indexVar);
        p.addAuxVariables(vars);
	}
	
	/**
	 * Create a new Constraint such as: <code> vars[indexVar]
	 * oper var </code>.
	 * 
	 * @param vars an array of integer variables
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the array "vars"
	 * @param oper an operator
	 * @param var the integer variable.
	 * 
	 * @throws RuntimeException if the posting fails
	 */
	public Element(
			javax.constraints.Var[] vars, 
			javax.constraints.Var indexVar, 
			String oper, 
			javax.constraints.Var var) {
		super(indexVar.getProblem());
		if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Problem p = (Problem) indexVar.getProblem();
		JSetL.Constraint element = new JSetL.Constraint();
		IntLVar z = new IntLVar(p.getFreshName());
		IntLVar index = (IntLVar) indexVar.getImpl();
		SolverClass solver = ((Solver) p.getSolver()).getSolverClass();
		ListOps listOps = new ListOps(solver);
		List<IntLVar> list = 
			new ArrayList<IntLVar>();
		for (int i = 0; i < vars.length; i++)
			list.add(i,(IntLVar) vars[i].getImpl());
		LList l = new LList("array", list);
		element.and(listOps.ithElem(l, index, z));
		JSetL.Constraint linear = null;
		if (oper.equals("=")) {
			linear = new JSetL.Constraint(
					element.and(z.eq((IntLVar)var.getImpl())));
        }
        if (oper.equals("!=")) {
        	linear = new JSetL.Constraint(
        			element.and(z.neq((IntLVar)var.getImpl())));
        }
        if (oper.equals(">")) {
        	linear = new JSetL.Constraint(
        			element.and(z.gt((IntLVar)var.getImpl())));
        }
        if (oper.equals(">=")) {
        	linear = new JSetL.Constraint(
        			element.and(z.ge((IntLVar)var.getImpl())));
        }
        if (oper.equals("<")) {
        	linear = new JSetL.Constraint(
        			element.and(z.lt((IntLVar)var.getImpl())));
        }
        if (oper.equals("<=")) {
        	linear = new JSetL.Constraint(
        			element.and(z.le((IntLVar)var.getImpl())));
        }
        setImpl(linear);
        p.addAuxVariable(z);
        p.addAuxVariable(indexVar);
        p.addAuxVariables(vars);
        p.addAuxVariable(var);
	}
	
	public void post() {
		((Problem) getProblem()).post(this);
	}
	
}
