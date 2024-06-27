package com.exigen.ie.constrainer.impl;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntVar;

public class Test1 {

	public static void main(String[] args) {
		Constrainer constrainer = new Constrainer("Test");

		//======= Define variables
		IntVar x = constrainer.addIntVar(0, 10, "X");
		IntVar y = constrainer.addIntVar(0, 10, "Y");
		IntVar z = constrainer.addIntVar(0, 10, "Z");
		IntExp costExp = x.mul(3).mul(y).sub(z.mul(4)); // Cost = 3XY - 4Z
		IntVar cost = constrainer.addIntVar(costExp);
		cost.name("cost");
		
		//======= Define constraints
		constrainer.addConstraint( x.lt(y) ); 		// X < Y
		constrainer.addConstraint( x.add(y).eq(z) ); 	// X + Y = Z
		constrainer.addConstraint( y.gt(5) ); 		// Y > 5
		constrainer.addConstraint( cost.gt(3) ); 		// cost > 3
		constrainer.addConstraint( cost.le(25) );    	// cost <= 25
		
		//======= Post all constraints
		System.out.println("Before Constraint Posting: " + constrainer.integers());
		System.out.println(cost);
		try {
			constrainer.postConstraints();
		} catch (Exception e) {
			System.out.println("ERROR to post");
		}
		
		System.out.println("After Constraint Posting: " + constrainer.integers());
		System.out.println(cost);
		System.out.println("costExp="+costExp);
		try {
			cost.eq(26).asConstraint().post();
		} catch (Exception e) {
			System.out.println("Fail to post cost == 26");
		}
		
			
		//======= Find a solution
		System.out.println("=== Find One solution:");
//		Solution solution = constrainer.solve();
//		if (solution == null)
//			constrainer.log("No Solutions");
//		else
//			solution.log();
//		System.out.println(("After Search " +constrainer.getVars());
	}
}