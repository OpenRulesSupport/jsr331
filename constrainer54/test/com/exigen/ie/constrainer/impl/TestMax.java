package com.exigen.ie.constrainer.impl;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

public class TestMax {

	/**
	 * Run 4 different tests using CSP.max(Var[] array),
	 * that only differ in the parameters of variable 'A':
	 * -- in test 1, 'A' has range  8, 8
	 * -- in test 2, 'A' has range  8,10
	 * -- in test 3, 'A' has range 10,10  <-- this is the one that failed
	 * -- in test 4, different numbers altogether, was identified as
	 *    a counter example to the first attempt at fixing the bug
	 * 
	 * Test 3 triggers an ArrayIndexOutOfBoundsException
	 * at IntExpArrayElement1:935, resulting in a
	 * NullPointerException at Var:120.
	 * 
	 * This was traced to an error in constrainer (IntExpArrayElement1.java:946),
	 * which this class tests by simulation of the CP-Inside code
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
			test1(); 
			test2();
			test3();
			test4();
	}

	/**
	 * This is a copy of the CP-Inside max constraint
	 *  
	 */
	static boolean computeMax(Constrainer c, IntExpArray array)
	{
		try {
			int m = array.get(0).min(); // the largest minimum
			int M = array.get(0).max(); // the largest maximum
			for (int i = 1; i < array.size(); i++) {
				IntExp var = array.get(i);
				int mini = var.min();
				int maxi = var.max();
				if (m < mini)
					m = mini;
				if (M < maxi)
					M = maxi;
			}
			
			IntVar result = c.addIntVar(m, M);
			for (int i = 0; i < array.size(); i++) {
				IntExp var = array.get(i);
				c.postConstraint(result.ge(var));
			}
			
			IntVar indexVar = c.addIntVar(0, array.size() - 1);
			result.eq(new IntExpArrayElement1(array, indexVar));
		} catch (Exception e) {
			System.out.println("Failure to create max(Var[])");
			return false;
		}
		return true;
	}
	
	static void test1()
	{
		System.out.println("Start test 1");
		Constrainer C = new Constrainer("TestIntExpElementAt");
			      
		IntExpArray array = new IntExpArray(C, 3);
		
		array.set(C.addIntVar(  8,  8), 0); // A
		array.set(C.addIntVar(  4, 10), 1); // B
		array.set(C.addIntVar(  7, 10), 2); // C

		if ( computeMax(C, array) )
			System.out.println("Test 1 finished successfully");
	}

	static void test2()
	{
		System.out.println("Start test 2");
		Constrainer C = new Constrainer("TestIntExpElementAt");
			      
		IntExpArray array = new IntExpArray(C, 3);
		
		array.set(C.addIntVar(  8, 10), 0); // A
		array.set(C.addIntVar(  4, 10), 1); // B
		array.set(C.addIntVar(  7, 10), 2); // C

		if ( computeMax(C, array) )
			System.out.println("Test 2 finished successfully");
	}

	static void test3()
	{
		System.out.println("Start test 3");
		Constrainer C = new Constrainer("TestIntExpElementAt");
			      
		IntExpArray array = new IntExpArray(C, 3);
		
		array.set(C.addIntVar( 10, 10), 0); // A
		array.set(C.addIntVar(  4, 10), 1); // B
		array.set(C.addIntVar(  7, 10), 2); // C

		if ( computeMax(C, array) )
			System.out.println("Test 3 finished successfully");
	}
	
	static void test4()
	{
		System.out.println("Start test 4");
		Constrainer C = new Constrainer("TestIntExpElementAt");
			      
		IntExpArray array = new IntExpArray(C, 3);
		
		array.set(C.addIntVar(  1,  6), 0); // A
		array.set(C.addIntVar(  1, 12), 1); // B
		array.set(C.addIntVar(  5, 13), 2); // C

		if ( computeMax(C, array) )
			System.out.println("Test 4 finished successfully");
	}
}
