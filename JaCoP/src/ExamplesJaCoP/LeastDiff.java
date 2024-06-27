/**
 *  LeastDiff.java 
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

package ExamplesJaCoP;

import java.util.ArrayList;

import JaCoP.constraints.Alldifferent;
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.XgtY;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Variable;

/**
 * Least Diff problem in JaCoP.
 *
 * Minimize the difference ABCDE - FGHIJ 
 *                     where A..J is all different in the range 0..9.
 *
 * The solution is: 50123 - 49876 = 247
 *
 * Other models of this problem:
 * MiniZinc model: http://www.hakank.org/minizinc/least_diff.mzn
 * Choco (version 1): http://www.hakank.org/constraints/LeastDiff2.java
 *
 * 
 * JaCoP Model by Hakan Kjellerstrand (hakank@bonetmail.com)
 * Also see http://www.hakank.org/JaCoP/
 * 
 * @author Hakan Kjellerstrand and Radoslaw Szymanek
 *
 */

public class LeastDiff extends Example {
    
 
    @Override
	public void model() {
        
    	// Creating constraint store . 
        // This object contains information about all the constraints and variables.    
        store = new FDstore();
        
        // Creating Variables (finite domain variables). 
        // There are as many variables as there are letters/digits.
        FDV a = new FDV(store, "a", 0, 9);
        FDV b = new FDV(store, "b", 0, 9);
        FDV c = new FDV(store, "c", 0, 9);
        FDV d = new FDV(store, "d", 0, 9);
        FDV e = new FDV(store, "e", 0, 9);
        FDV f = new FDV(store, "f", 0, 9);
        FDV g = new FDV(store, "g", 0, 9);
        FDV h = new FDV(store, "h", 0, 9);
        FDV i = new FDV(store, "i", 0, 9);
        FDV j = new FDV(store, "j", 0, 9);
        
        cost = new FDV(store, "diff", 0, 99999);    
        
        // Creating arrays for FDVs
        FDV digits[] = { a,b,c,d,e,f,g,h,i,j };
        FDV abcde[]  = { a,b,c,d,e };
        FDV fghij[]  = { f,g,h,i,j };
        
        // Creating and imposing constraints
        
        // Imposing inequalities constraints between letters
        // Only one global constraint to make sure that all digits are different.
        store.impose(new Alldifferent(digits));
        
        int[] weights5 = { 10000, 1000, 100, 10, 1 };
        FDV value_abcde = new FDV(store, "v_abcde", 0, 99999);
        FDV value_fghij = new FDV(store, "v_fghij", 0, 99999);
        
        // Constraints for getting value for words
        store.impose(new SumWeight (abcde, weights5, value_abcde));
        store.impose(new SumWeight (fghij, weights5, value_fghij));
        
        // abcde > fghij
        store.impose(new XgtY (value_abcde, value_fghij));
        
        
        // Main equation of the problem:
        //    diff = abcde - fghij
        //  -> 
        //    diff + fghij = abcde
        // It would be niced with a constraint XminusYeqZ(...), though
        store.impose(new XplusYeqZ (cost, value_fghij, value_abcde));
    
        vars = new ArrayList<Variable>();
        for (Variable v : digits) vars.add(v);
        
    }

    
    /**
     * It executes the program which solves this simple optimization problem.
     * @param args
     */
    public static void main(String args[]) {

    	LeastDiff example = new LeastDiff();
    	
    	example.model();
    	
    	example.searchSmallestDomain(true);
    	
    }
    
} 
