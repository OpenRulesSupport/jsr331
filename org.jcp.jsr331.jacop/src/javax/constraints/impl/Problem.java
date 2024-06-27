/**
 *  Problem.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  Notwithstanding any other provision of this License, the copyright
 *  owners of this work supplement the terms of this License with terms
 *  prohibiting misrepresentation of the origin of this work and requiring
 *  that modified versions of this work be marked in reasonable ways as
 *  different from the original version. This supplement of the license
 *  terms is in accordance with Section 7 of GNU Affero General Public
 *  License version 3.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package javax.constraints.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.constraints.Constraint;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.impl.constraint.AllDifferent;

import org.apache.commons.logging.LogFactory;

import JaCoP.constraints.And;
import JaCoP.constraints.ElementInteger;
import JaCoP.constraints.GCC;
import JaCoP.constraints.Sum;
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XgtC;
import JaCoP.constraints.XgtY;
import JaCoP.constraints.XgteqC;
import JaCoP.constraints.XgteqY;
import JaCoP.constraints.XltC;
import JaCoP.constraints.XltY;
import JaCoP.constraints.XlteqC;
import JaCoP.constraints.XlteqY;
import JaCoP.constraints.XneqC;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;

/**
 * @author Radoslaw Szymanek
 *
 */

public class Problem extends AbstractProblem {
	
	/**
	 * CP API Reference Implementation with Constrainer - release version number
	 */
	static public final String JSR331_JACOP_VERSION = "JSR-331 Implementation based on JaCoP 3.0";
	

	//public Logger LOGGER;
	public static org.apache.commons.logging.Log LOGGER = LogFactory.getLog("javax.constraints");;
	
	public Store store;

	public Problem() {
		super("");
		store = new Store();
		//LOGGER = Logger.getLogger("log.txt");
	}
	
	public Problem(String id) {
		
		super(id);
		
		store = new Store();
		store.setID(id);
		//LOGGER = Logger.getLogger("log-" + id + ".txt");
		
	}
	
	@Override
	public Constraint and(Constraint[] array) {
		
		ArrayList<PrimitiveConstraint> list = new ArrayList<PrimitiveConstraint>();
		
		for (Constraint current : array) {
			if (!(current.getImpl() instanceof PrimitiveConstraint))
				throw new RuntimeException("Constraint " + current + 
						" has no implementation for the method and." +
						" It cannot be used in logical expressions.");
			else
				list.add((PrimitiveConstraint)current);
		}

		return new javax.constraints.impl.Constraint(this, new And(list));
		
	}

	@Override
	public Constraint post(int[] array, Var[] vars, String oper, int value) {
		
		return post(array, vars, oper, this.variable(value, value));

	}

	@Override
	public Constraint post(int[] array, Var[] vars, String oper, Var var) {
		
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

		if (oper.equals("=")) {

			javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, new SumWeight(variables, array, (IntVar)var.getImpl()));

			c.post();
			return c;


		}
		else {

			Var sum = variable(JaCoP.core.IntDomain.MinInt , JaCoP.core.IntDomain.MaxInt);

			JaCoP.constraints.Constraint[] list = new JaCoP.constraints.Constraint[2];
			
			list[0] = new SumWeight(variables, array, (IntVar)sum.getImpl());

			if (oper.equals(">="))
				list[1] = new XgteqY((IntVar)sum.getImpl(), (IntVar)var.getImpl());
			
			if (oper.equals(">"))
				list[1] = new XgtY((IntVar)sum.getImpl(), (IntVar)var.getImpl());
			
			if (oper.equals("<="))
				list[1] = new XgteqY((IntVar)var.getImpl(), (IntVar)sum.getImpl());
			
			if (oper.equals("<"))
				list[1] = new XgtY((IntVar)var.getImpl(), (IntVar)sum.getImpl());

			if (oper.equals("!="))
					list[1] = new XneqY((IntVar)sum.getImpl(), (IntVar)var.getImpl()); 

			javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, list); 
			c.post();
			return c;

		}

	}

	@Override
	public Constraint post(Var var, String oper, int value) {

		Constraint c = linear(var, oper, value);
		c.post();
		return c;
		
	}

	
	@Override
	public Constraint post(Var var1, String oper, Var var2) {

		Constraint c = linear(var1, oper, var2);
		c.post();
		return c;
		
	}

	@Override
	public Constraint post(Var[] vars, String oper, int value) {
		
		return post(vars, oper, this.variable(value, value));

	}

	@Override
	public Constraint post(Var[] vars, String oper, Var var) {

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

		if (oper.equals("=")) {

			javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, new Sum(variables, (IntVar)var.getImpl())); 
			c.post();
			return c;

		}
		else {

			Var sum = variable(JaCoP.core.IntDomain.MinInt , JaCoP.core.IntDomain.MaxInt);

			JaCoP.constraints.Constraint[] list = new JaCoP.constraints.Constraint[2];
			
			list[0] = new Sum(variables, (IntVar)sum.getImpl());

			if (oper.equals(">="))
				list[1] = new XgteqY((IntVar)sum.getImpl(), (IntVar)var.getImpl());
			
			if (oper.equals(">"))
				list[1] = new XgtY((IntVar)sum.getImpl(), (IntVar)var.getImpl());
			
			if (oper.equals("<="))
				list[1] = new XgteqY((IntVar)var.getImpl(), (IntVar)sum.getImpl());
			
			if (oper.equals("<"))
				list[1] = new XgtY((IntVar)var.getImpl(), (IntVar)sum.getImpl());

			if (oper.equals("!="))
					list[1] = new XneqY((IntVar)sum.getImpl(), (IntVar)var.getImpl()); 

			javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, list); 
			c.post();
			return c;

		}

	}

	@Override
	public Constraint allDiff(Var[] vars) {
	
		javax.constraints.impl.Constraint c = new AllDifferent(vars);
		return c;
		
	}

	
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, Var var) {

		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar counter = (IntVar)p.variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();
	    	    
        array[0] = new JaCoP.constraints.Count(variables, counter, cardValue);
    			
		IntVar y = (IntVar)var.getImpl();
		
		if (oper.equals("=")) {
			array[1] = new XeqY(counter, y);
		}
		if (oper.equals("<")) {
			array[1] = new XltY(counter, y);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqY(counter, y);
		}
		if (oper.equals(">")) {
			array[1] = new XgtY(counter, y);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqY(counter, y);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqY(counter, y);
		}
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array); 
		c.post();
		return c;

	}
	
	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, Var var) {
		
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar counter = (IntVar)p.variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();

	    IntVar[] ys = {(IntVar)cardVar.getImpl()};
        array[0] = new JaCoP.constraints.AmongVar(variables, ys, counter);
        	
		IntVar y = (IntVar)var.getImpl();
		
		if (oper.equals("=")) {
			array[1] = new XeqY(counter, y);
		}
		if (oper.equals("<")) {
			array[1] = new XltY(counter, y);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqY(counter, y);
		}
		if (oper.equals(">")) {
			array[1] = new XgtY(counter, y);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqY(counter, y);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqY(counter, y);
		}
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array); 
		c.post();
		return c;

	}
	
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, int value) {


		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar counter = (IntVar)p.variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();
	    	    
        array[0] = new JaCoP.constraints.Count(variables, counter, cardValue);
    			
        if (oper.equals("=")) {
			array[1] = new XeqC(counter, value);
		}
		if (oper.equals("<")) {
			array[1] = new XltC(counter, value);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqC(counter, value);
		}
		if (oper.equals(">")) {
			array[1] = new XgtC(counter, value);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqC(counter, value);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqC(counter, value);
		}
                
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array); 
		c.post();
		return c;

	}
	

	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, int value) {
		
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar counter = (IntVar)p.variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();

	    IntVar[] ys = {(IntVar)cardVar.getImpl()};
        array[0] = new JaCoP.constraints.AmongVar(variables, ys, counter);
        	
        if (oper.equals("=")) {
			array[1] = new XeqC(counter, value);
		}
		if (oper.equals("<")) {
			array[1] = new XltC(counter, value);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqC(counter, value);
		}
		if (oper.equals(">")) {
			array[1] = new XgtC(counter, value);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqC(counter, value);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqC(counter, value);
		}
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array); 
		c.post();
		return c;

	}

	@Override
	public Constraint postElement(int[] vars, Var indexVar, String oper, int value) {
		
	    Var varValue = variable(IntDomain.MinInt, IntDomain.MaxInt);

		// COMMENT, Standard does not support any index shift.
	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];

	    array[0] = new JaCoP.constraints.Element((IntVar)indexVar.getImpl(), 
	    		vars, 
	    		(IntVar)varValue.getImpl(), 
	    		-1);

	    IntVar var = (IntVar)varValue.getImpl();

	    if (oper.equals("=")) {
	    	array[1] = new XeqC(var, value);
	    }
	    if (oper.equals("<")) {
	    	array[1] = new XltC(var, value);
	    }
	    if (oper.equals("<=")) {
	    	array[1] = new XlteqC(var, value);
	    }
	    if (oper.equals(">")) {
	    	array[1] = new XgtC(var, value);
	    }
	    if (oper.equals(">=")) {
	    	array[1] = new XgteqC(var, value);
	    }
	    if (oper.equals("!=")) {
	    	array[1] = new XneqC(var, value);
	    }

	    javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array);
	    c.post();
	    return c;

	}

	@Override
	public Constraint postElement(int[] array, Var indexVar, String oper, Var var) {
		
		// COMMENT, Standard does not support any index shift.
		if (oper.equals("=")) {


			// Standard works from 0..n-1, JaCoP works from 1..n, need offset -1.
			javax.constraints.impl.Constraint c = 
				new javax.constraints.impl.Constraint(this, new ElementInteger((IntVar)indexVar.getImpl(), 
						  														array, 
						  														(IntVar)var.getImpl(), 
						  														-1));
			c.post();
			return c;
			
		}
		else {
			
			Var valueVar = variable(IntDomain.MinInt, IntDomain.MaxInt);
			
			JaCoP.constraints.Constraint[] list = new JaCoP.constraints.Constraint[2];
			
			list[0] = new ElementInteger((IntVar)indexVar.getImpl(), 
						  array, 
   						  (IntVar)valueVar.getImpl(), 
   						  -1);
			
			if (oper.equals(">="))
				list[1] = new XgteqY((IntVar)valueVar.getImpl(), (IntVar)var.getImpl());
		
			if (oper.equals(">"))
				list[1] = new XgtY((IntVar)valueVar.getImpl(), (IntVar)var.getImpl());
	
			if (oper.equals("<="))
				list[1] = new XgteqY((IntVar)var.getImpl(), (IntVar)valueVar.getImpl());

			if (oper.equals("<"))
				list[1] = new XgtY((IntVar)var.getImpl(), (IntVar)valueVar.getImpl());

			if (oper.equals("!="))
				list[1] = new XneqY((IntVar)valueVar.getImpl(), (IntVar)var.getImpl());

			javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, list);
			c.post();
			return c;
			
		}
		
	}
	
	@Override
	public Constraint postElement(Var[] vars, Var indexVar, String oper, int value) {
		
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    Var varValue = variable(IntDomain.MinInt, IntDomain.MaxInt);

		// COMMENT, Standard does not support any index shift.

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];

	    array[0] = new JaCoP.constraints.Element((IntVar)indexVar.getImpl(), 
	    		variables, 
	    		(IntVar)varValue.getImpl(), 
	    		-1);

	    IntVar var = (IntVar)varValue.getImpl();

	    if (oper.equals("=")) {
	    	array[1] = new XeqC(var, value);			
	    }
	    if (oper.equals("<")) {
	    	array[1] = new XltC(var, value);
	    }
	    if (oper.equals("<=")) {
	    	array[1] = new XlteqC(var, value);
	    }
	    if (oper.equals(">")) {
	    	array[1] = new XgtC(var, value);
	    }
	    if (oper.equals(">=")) {
	    	array[1] = new XgteqC(var, value);
	    }
	    if (oper.equals("!=")) {
	    	array[1] = new XneqC(var, value);
	    }

	    javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array);
	    c.post();
	    return c;

	}
	
	@Override
	public Constraint postElement(Var[] vars, Var indexVar, String oper, Var var) {
		
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

		// COMMENT, Standard does not support any index shift.
		if (oper.equals("=")) {


			// Standard works from 0..n-1, JaCoP works from 1..n, need offset -1.
			javax.constraints.impl.Constraint c = 
				new javax.constraints.impl.Constraint(this, new JaCoP.constraints.Element((IntVar)indexVar.getImpl(), 
						  														variables,
						  														(IntVar)var.getImpl(), 
						  														-1));
			c.post();
			return c;
			
		}
		else {
			
			Var valueVar = variable(IntDomain.MinInt, IntDomain.MaxInt);
			
			JaCoP.constraints.Constraint[] list = new JaCoP.constraints.Constraint[2];
			
			list[0] = new JaCoP.constraints.Element((IntVar)indexVar.getImpl(), 
						  variables, 
   						  (IntVar)valueVar.getImpl(), 
   						  -1);
			
			if (oper.equals(">="))
				list[1] = new XgteqY((IntVar)valueVar.getImpl(), (IntVar)var.getImpl());
		
			if (oper.equals(">"))
				list[1] = new XgtY((IntVar)valueVar.getImpl(), (IntVar)var.getImpl());
	
			if (oper.equals("<="))
				list[1] = new XgteqY((IntVar)var.getImpl(), (IntVar)valueVar.getImpl());

			if (oper.equals("<"))
				list[1] = new XgtY((IntVar)var.getImpl(), (IntVar)valueVar.getImpl());

			if (oper.equals("!="))
				list[1] = new XneqY((IntVar)valueVar.getImpl(), (IntVar)var.getImpl());

			javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, list);
			c.post();
			return c;
			
		}
		
	}

	@Override
	public Constraint postGlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
		
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();

		HashMap<Integer, Integer> valuePositionMaping = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < values.length; i++)
			valuePositionMaping.put(values[i], i);
		
		IntervalDomain sum = new IntervalDomain();
		for (int i = 0; i < vars.length; i++) {
			sum.unionAdapt( ((IntVar)vars[i].getImpl()).domain);
		}
		
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

		IntVar[] counter = new IntVar[sum.getSize()]; 
		ValueEnumeration enumer = sum.valueEnumeration();
		int i = 0;
		while (enumer.hasMoreElements()) {
			int value = enumer.nextElement();
			Integer position = valuePositionMaping.get(value);
			if (position == null) {
				counter[i] = (IntVar)p.variable(0, vars.length).getImpl();
			}
			else {
				counter[i] = (IntVar)p.variable(cardMin[position], cardMax[position]).getImpl();				
			}
			i++;
		}
		
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, new GCC(variables, counter));
		c.post();
		return c;

	}

	@Override
	public Constraint postGlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {

		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();

		HashMap<Integer, Integer> valuePositionMaping = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < values.length; i++)
			valuePositionMaping.put(values[i], i);
		
		IntervalDomain sum = new IntervalDomain();
		for (int i = 0; i < vars.length; i++) {
			sum.unionAdapt( ((IntVar)vars[i].getImpl()).domain);
		}
		
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

		IntVar[] counter = new IntVar[sum.getSize()]; 
		ValueEnumeration enumer = sum.valueEnumeration();
		int i = 0;
		while (enumer.hasMoreElements()) {
			int value = enumer.nextElement();
			Integer position = valuePositionMaping.get(value);
			if (position == null) {
				counter[i] = (IntVar)p.variable(0, vars.length).getImpl();
			}
			else {
				counter[i] = (IntVar)cardinalityVars[position].getImpl();				
			}
			i++;
		}
		
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, new GCC(variables, counter));
		c.post();
		return c;
		
	}
	
	public Constraint constraintGlobalCardinality(Var[] vars, Var[] cardinalityVars) {
		
		IntVar[] xVariables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			xVariables[i] = (IntVar)vars[i].getImpl();

		IntVar[] cVariables = new IntVar[cardinalityVars.length];		
		for (int i = 0; i < cardinalityVars.length; i++)
			cVariables[i] = (IntVar)cardinalityVars[i].getImpl();
		
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, new GCC(xVariables, cVariables));
		c.post();
		return c;

	}

	
	@Override
	public Constraint postMax(Var[] vars, String oper, int value) {

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar sum = (IntVar)variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();
        array[0] = new JaCoP.constraints.Max(variables, sum);
    			
		if (oper.equals("=")) {
			array[1] = new XeqC(sum, value);
		}
		if (oper.equals("<")) {
			array[1] = new XltC(sum, value);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqC(sum, value);
		}
		if (oper.equals(">")) {
			array[1] = new XgtC(sum, value);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqC(sum, value);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqC(sum, value);
		}

	    javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array);
	    c.post();
	    return c;

	}

	
	@Override
	public Constraint postMax(Var[] vars, String oper, Var var) {
	
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar sum = (IntVar)variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();
        array[0] = new JaCoP.constraints.Max(variables, sum);
    			
		IntVar y = (IntVar)var.getImpl();
		
		if (oper.equals("=")) {
			array[1] = new XeqY(sum, y);
		}
		if (oper.equals("<")) {
			array[1] = new XltY(sum, y);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqY(sum, y);
		}
		if (oper.equals(">")) {
			array[1] = new XgtY(sum, y);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqY(sum, y);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqY(sum, y);
		}

	    javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array);
	    c.post();
	    return c;
		
	}

	@Override
	public Constraint postMin(Var[] vars, String oper, int value) {

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar sum = (IntVar)variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();
        array[0] = new JaCoP.constraints.Min(variables, sum);
    			
		if (oper.equals("=")) {
			array[1] = new XeqC(sum, value);
		}
		if (oper.equals("<")) {
			array[1] = new XltC(sum, value);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqC(sum, value);
		}
		if (oper.equals(">")) {
			array[1] = new XgtC(sum, value);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqC(sum, value);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqC(sum, value);
		}

	    javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array);
	    c.post();
	    return c;

	}

	@Override
	public Constraint postMin(Var[] vars, String oper, Var var) {

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[2];
	    IntVar sum = (IntVar)variable(IntDomain.MinInt, IntDomain.MaxInt).getImpl();
        array[0] = new JaCoP.constraints.Min(variables, sum);
    			
		IntVar y = (IntVar)var.getImpl();
		
		if (oper.equals("=")) {
			array[1] = new XeqY(sum, y);
		}
		if (oper.equals("<")) {
			array[1] = new XltY(sum, y);
		}
		if (oper.equals("<=")) {
			array[1] = new XlteqY(sum, y);
		}
		if (oper.equals(">")) {
			array[1] = new XgtY(sum, y);
		}
		if (oper.equals(">=")) {
			array[1] = new XgteqY(sum, y);
		}
		if (oper.equals("!=")) {
			array[1] = new XneqY(sum, y);
		}

	    javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, array);
	    c.post();
	    return c;
		
	}

	@Override
	protected Solver createSolver() {
		
		return new javax.constraints.impl.search.Solver(this, this.store);

	}

	@Override
	public void debug(String text) {
		
		//LOGGER.log(Level.FINE, text);
		LOGGER.debug(text);
		
	}

	@Override
	public Var element(int[] array, Var indexVar) {

	    Var value = variable(IntDomain.MinInt, IntDomain.MaxInt);
	    
	    JaCoP.constraints.Constraint constraint = 
	    	(JaCoP.constraints.Constraint) new JaCoP.constraints.Element((IntVar)indexVar.getImpl(), array, (IntVar)value.getImpl());
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		c.post();

		return value;
		
	}

	@Override
	public Var element(Var[] vars, Var indexVar) {

	    Var value = variable(IntDomain.MinInt, IntDomain.MaxInt);
	    
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint constraint = 
	    	(JaCoP.constraints.Constraint) new JaCoP.constraints.Element((IntVar)indexVar.getImpl(), variables, (IntVar)value.getImpl());
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		c.post();

		return value;

	}

	@Override
	public void error(String text) {

		//LOGGER.log(Level.SEVERE, text);
		LOGGER.error(text);
		
	}

	@Override
	public String getImplVersion() {
		return JSR331_JACOP_VERSION;
	}

	@Override
	public Constraint linear(Var var, String oper, int value) {
		
		JaCoP.constraints.Constraint constraint = null;
		
		if (oper.equals(">="))	
			constraint = new XgteqC((IntVar)var.getImpl(), value);
		if (oper.equals(">"))
			constraint = new XgtC((IntVar)var.getImpl(), value);
		if (oper.equals("<="))
			constraint = new XlteqC((IntVar)var.getImpl(), value);
		if (oper.equals("<"))
			constraint = new XltC((IntVar)var.getImpl(), value);
		if (oper.equals("!="))
			constraint = new XneqC((IntVar)var.getImpl(), value);
		if (oper.equals("="))
			constraint = new XeqC((IntVar)var.getImpl(), value);

		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint); 

		//c.post();
		return c;

	}

	@Override
	public Constraint linear(Var var1, String oper, Var var2) {
		
		JaCoP.constraints.Constraint constraint = null;

		if (oper.equals("="))
			constraint = new XeqY((IntVar)var1.getImpl(), (IntVar)var2.getImpl());

		if (oper.equals(">="))
			constraint = new XgteqY((IntVar)var1.getImpl(), (IntVar)var2.getImpl());
	
		if (oper.equals(">"))
			constraint = new XgtY((IntVar)var1.getImpl(), (IntVar)var2.getImpl());

		if (oper.equals("<="))
			constraint = new XlteqY((IntVar)var1.getImpl(), (IntVar)var2.getImpl());

		if (oper.equals("<"))
			constraint = new XltY((IntVar)var1.getImpl(), (IntVar)var2.getImpl());

		if (oper.equals("!="))
			constraint = new XneqY((IntVar)var1.getImpl(), (IntVar)var2.getImpl());

		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		//c.post();
		return c;

	}

	@Override
	public void loadFromXML(InputStream in) throws Exception {
		assert false;
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void log(String text) {

		//LOGGER.log(Level.INFO, text);
		LOGGER.info(text);
		
	}

	public Var max(Var var1, Var var2) {

		IntVar[] variables = new IntVar[2];		
		variables[0] = (IntVar)var1.getImpl();
		variables[1] = (IntVar)var2.getImpl();

	    Var max = variable(IntDomain.MinInt, IntDomain.MaxInt);
                
	    JaCoP.constraints.Constraint constraint = new JaCoP.constraints.Max(variables, (IntVar)max.getImpl());
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		c.post();

		return max;
	}

	public Var max(Var[] vars) {

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    Var min = variable(IntDomain.MinInt, IntDomain.MaxInt);
                
	    JaCoP.constraints.Constraint constraint = new JaCoP.constraints.Max(variables, (IntVar)min.getImpl());
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		c.post();

		return min;

	}

	public Var min(Var var1, Var var2) {

		IntVar[] variables = new IntVar[2];		
		variables[0] = (IntVar)var1.getImpl();
		variables[1] = (IntVar)var2.getImpl();

	    Var min = variable(IntDomain.MinInt, IntDomain.MaxInt);
                
	    JaCoP.constraints.Constraint constraint = new JaCoP.constraints.Min(variables, (IntVar)min.getImpl());
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		c.post();

		return min;

	}
	
	public Var min(Var[] vars) {

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    Var min = variable(IntDomain.MinInt, IntDomain.MaxInt);
                
	    JaCoP.constraints.Constraint constraint = new JaCoP.constraints.Min(variables, (IntVar)min.getImpl());
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		c.post();

		return min;

	}

	@Override
	public void post(Constraint constraint) {
		
		JaCoP.constraints.Constraint[] array = (JaCoP.constraints.Constraint[]) constraint.getImpl();
		
		for (JaCoP.constraints.Constraint current : array) {
			
			if (current instanceof JaCoP.constraints.Constraint) {
				store.impose( current );
				continue;
			}
		
			if (current instanceof JaCoP.constraints.DecomposedConstraint) {
				store.imposeDecomposition( current );
				continue;
			}
			
			assert false;
			throw new RuntimeException("Not implemented");

		}
	
	}
	
	@Override
	public Var scalProd(int[] arrayOfValues, Var[] vars) {
		
		long min = 0;
		boolean minReached = false;
		long max = 0;
		boolean maxReached = false;
		
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++) {
			variables[i] = (IntVar)vars[i].getImpl();
			min += variables[i].min() * arrayOfValues[i];
			if (min < IntDomain.MinInt)
				minReached = true;
			max += variables[i].max() * arrayOfValues[i];
			if (max > IntDomain.MaxInt)
				maxReached = true;
		}

		if (minReached)
			min = IntDomain.MinInt;
		if (maxReached)
			max = IntDomain.MaxInt;
		
		javax.constraints.Var auxilary = this.variable( (int)min, (int)max);

		new javax.constraints.impl.Constraint(this, new SumWeight(variables, arrayOfValues,
					(IntVar)auxilary.getImpl())).post();

		return auxilary;
		
	}

	@Override
	public void storeToXML(OutputStream os, String comment) throws Exception {
		assert false;
		throw new RuntimeException("Not implemented");
	}

	public Var sum(Var[] vars) {

		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    Var sum = variable(IntDomain.MinInt, IntDomain.MaxInt);
                
	    JaCoP.constraints.Constraint constraint = new JaCoP.constraints.Sum(variables, (IntVar)sum.getImpl());
        
		javax.constraints.impl.Constraint c = new javax.constraints.impl.Constraint(this, constraint);
		c.post();

		return sum;

	}

	@Override
	public Var variable(int min, int max) {

		return this.add( new javax.constraints.impl.Var(this, min, max) );

	}

	@Override
	public Var createVariable(String name, int min, int max) {

		return new javax.constraints.impl.Var(this, name, min, max);

	}

	@Override
	public Var variable(String name, int[] domain) {

		return this.add( new javax.constraints.impl.Var(this, name, domain) );

	}

	@Override
	public VarBool variableBool(String name) {
		
		return this.add( new javax.constraints.impl.VarBool(this, name) );
		
	}

}
