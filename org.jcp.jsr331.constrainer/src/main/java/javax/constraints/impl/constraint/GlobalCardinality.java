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
import javax.constraints.impl.Problem;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Constraint;
import com.exigen.ie.constrainer.ConstraintImpl;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.IntArrayCards;
import com.exigen.ie.constrainer.IntExpArray;

/**
 * This constraint states that the number of constrained variables in the array
 * "cardinalities" is equal to the number of occurrences of
 * the values in the array "values" in the array "vars". More precisely, for each i,
 * cardinalities[i] is equal to the number of occurrences of values[i] in the
 * array vars. After propagation of this constraint, the minimum of
 * cardinalities[i] is at least equal to the number of variables contained in
 * vars bound to the value at values[i]; and the maximum of cardinalities[i] is
 * at most equal to the number of variables in vars that contain the value at
 * values[i] in their domain. The arrays cardinalities and values must be the
 * same length, otherwise an exception will be thrown.
 * 
 * This implementation should at least overload the method "post" to avoid
 * a primitive implementation from the super class
 *
 */

public class GlobalCardinality extends javax.constraints.impl.Constraint {
	
	Var[] cardVars;
	Var[] vars;
	int[] values;
	Var[] valueVars;
	
	public GlobalCardinality(Var[] vars, Var[] cardVars, int[] values) {
		super(vars[0].getProblem());
		this.cardVars = cardVars;
		this.vars = vars;
		this.values = values;
		this.valueVars = null;
		doIt();
	}
	
	public GlobalCardinality(Var[] vars, Var[] cardVars) {
		super(vars[0].getProblem());
		this.cardVars = cardVars;
		this.vars = vars;
		this.values = new int[cardVars.length];
		for (int i = 0; i < cardVars.length; i++) {
			values[i] = i;
		}
		this.valueVars = null;
		doIt();
	}
	
	public GlobalCardinality(Var[] vars, Var[] cardVars, Var[] valueVars) {
		super(vars[0].getProblem());
		this.cardVars = cardVars;
		this.vars = vars;
		this.values = null;
		this.valueVars = valueVars;
		doIt();
	}
	
	void doIt() {
		int l1 = cardVars.length ;
		int l2;
		if (values != null)
			l2 = values.length;
		else
			l2 = valueVars.length;			
		if (l1 != l2)
			throw new RuntimeException(
					"Invalid array lengths in the GlobalCardinalityI");
		if (values != null)
			defineNativeImpl(vars, cardVars, values);
		else
			defineNativeImpl(vars, cardVars, valueVars);
	}
	

	
	public void defineNativeImpl(Var[] vars, Var[] cardVars, int[] values) {
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray cVars = problem.getExpArray(vars);		
		IntExpArray cCardinalityVars = problem.getExpArray(cardVars);
		try {
			IntExpArray cards = constrainer.distribute(cVars,values);
			Constraint newC = new ConstrainerIntExpArrayEq(cards,cCardinalityVars);
			setImpl(constrainer.addConstraint(newC));
		} catch (Exception f) {
			throw new RuntimeException(
					"Failure to create GlobalCardinality constraint");
		}		
	}
	
	public void defineNativeImpl(Var[] vars, Var[] cardVars, Var[] valueVars) {
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray cVars = problem.getExpArray(vars);
		IntExpArray cValueVars = problem.getExpArray(valueVars);
		IntExpArray cCardVars = problem.getExpArray(valueVars);
		try {
			IntArrayCards givenCards = cCardVars.cards();
			IntArrayCards distributedCards = constrainer.distribute(cVars,cValueVars);
			Constraint newC = new ConstrainerArrayCardsEq(distributedCards,givenCards);
			setImpl(constrainer.addConstraint(newC));
		} catch (Exception f) {
			throw new RuntimeException(
					"Failure to create GlobalCardinality constraint");
		}
	}
	
	class ConstrainerArrayCardsEq extends ConstraintImpl {
		IntArrayCards cards1, cards2;
		
		public ConstrainerArrayCardsEq(IntArrayCards cards1, IntArrayCards cards2) {
			super(cards1.cardAt(0).constrainer());
			this.cards1 = cards1;
			this.cards2 = cards2;
		}
		
		public Goal execute() throws Failure {
			for(int i= 0; i < cards1.cardSize(); i++) {
				constrainer().postConstraint(cards1.cardAt(i).eq(cards2.cardAt(i)));
			}
			return null;
		}
	}
	
	class ConstrainerIntExpArrayEq extends ConstraintImpl {
		IntExpArray arr1, arr2;
		
		public ConstrainerIntExpArrayEq(IntExpArray arr1, IntExpArray arr2) {
			super(arr1.elementAt(0).constrainer());
			this.arr1 = arr1;
			this.arr2 = arr2;
		}
		
		public Goal execute() throws Failure {
			for(int i= 0; i < arr1.size(); i++) {
				constrainer().postConstraint(arr1.elementAt(i).eq(arr2.elementAt(i)));
			}
			return null;
		}
	}

	
}
