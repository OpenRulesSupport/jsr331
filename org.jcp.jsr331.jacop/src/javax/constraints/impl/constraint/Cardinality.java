/**
 *  Cardinality.java 
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

package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;

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
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;

/**
 * @author Radoslaw Szymanek
 *
 */

public class Cardinality extends Constraint {

	public Cardinality(Var[] vars, int cardValue, String oper, Var var) {
		
		super(vars[0].getProblem());

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
        
		setImpl(array);

	}
	
	public Cardinality(Var[] vars, Var cardVar, String oper, Var var) {
		
		super(vars[0].getProblem());

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
        
		setImpl(array);

	}
	
	public Cardinality(Var[] vars, int cardValue, String oper, int value) {

		super(vars[0].getProblem());

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
                
		setImpl(array);

	}
	

	public Cardinality(Var[] vars, Var cardVar, String oper, int value) {
		
		super(vars[0].getProblem());

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
        
		setImpl(array);

	}
		
}
