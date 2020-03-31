//================================================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1 
// 
// CONSTRAINER-BASED REFERENCE IMPLEMENTATION
//
// Copyright (c) Cork Constraint Computation Centre, 2010
// University College Cork, Cork, Ireland, www.4c.ucc.ie
// Constrainer is copyrighted by Exigen Group, USA.
// 
//================================================================
package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.IntArray;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;

public class Element extends Constraint {
	
	static final String name = "Element";
	
	public Element(int[] array, Var indexVar, String oper, int value) {
		super(indexVar.getProblem(),name);
		Problem problem = (Problem) indexVar.getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntArray intValues = new IntArray(constrainer, array.length);
		for (int i = 0; i < array.length; i++) {
			intValues.set(array[i], i);
		}
		try {
			IntExp element = intValues.elementAt((IntExp) indexVar.getImpl());
			problem.defineConstraintImpl(this, element, oper, value);
		} 
		catch (Exception f) {
			throw new RuntimeException("Failure to create constraint "+name);
		}
	}
	
	public Element(int[] array, Var indexVar, String oper, Var var) {
		super(indexVar.getProblem(),name);
		Problem problem = (Problem) indexVar.getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntArray intValues = new IntArray(constrainer, array.length);
		for (int i = 0; i < array.length; i++) {
			intValues.set(array[i], i);
		}
		try {
			IntExp element = intValues.elementAt((IntExp) indexVar.getImpl());
			problem.defineConstraintImpl(this, element, oper, var);
		} 
		catch (Exception f) {
			throw new RuntimeException("Failure to create constraint "+name);
		}
	}
	
	public Element(Var[] vars, Var indexVar, String oper, int value) {
		super(indexVar.getProblem(),name);
		Problem problem = (Problem) indexVar.getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp exp = (IntExp)vars[i].getImpl();
			intvars.set(exp, i);
		}
		try {
			IntExp element = intvars.elementAt((IntExp) indexVar.getImpl());
			problem.defineConstraintImpl(this, element, oper, value);
		} 
		catch (Exception f) {
			throw new RuntimeException("Failure to create constraint "+name);
		}
	}
	
	public Element(Var[] vars, Var indexVar, String oper, Var var) {
		super(indexVar.getProblem(),name);
		Problem problem = (Problem) indexVar.getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp exp = (IntExp)vars[i].getImpl();
			intvars.set(exp, i);
		}
		try {
			IntExp element = intvars.elementAt((IntExp) indexVar.getImpl());
			problem.defineConstraintImpl(this, element, oper, var);
		} 
		catch (Exception f) {
			throw new RuntimeException("Failure to create constraint "+name);
		}
	}
}
