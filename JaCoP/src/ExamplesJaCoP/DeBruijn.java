/**
 *  DeBruijn.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Hakan Kjellerstrand and Radoslaw Szymanek
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
import JaCoP.constraints.Min;
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.XeqY;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Variable;

/**
 * 
 * @author Hakan Kjellerstrand (hakank@bonetmail.com) and Radoslaw Szymanek
 * 
 * de Bruijn sequences in JaCoP.
 *
 * both "normal" and "arbitrary" de Bruijn sequences.
 * 
 * This is a port from my MiniZinc model
 *    http://www.hakank.org/minizinc/debruijn_binary.mzn
 *
 * and is explained somewhat in the swedish blog post
 * "Constraint Programming: Minizinc, Gecode/flatzinc och ECLiPSe/minizinc"
 * http://www.hakank.org/webblogg/archives/001209.html
 *
 * Related programs:
 * - "Normal" de Bruijn sequences
 *  CGI program for calculating the sequences
 *  http://www.hakank.org/comb/debruijn.cgi
 *  http://www.hakank.org/comb/deBruijnApplet.html (as Java applet)
 *
 * 
 * - "Arbitrary" de Bruijn sequences
 *   Program "de Bruijn arbitrary sequences"
 *   http://www.hakank.org/comb/debruijn_arb.cgi
 *
 *   This (swedish) blog post explains the program:
 *   "de Bruijn-sekvenser av godtycklig längd"
 *   http://www.hakank.org/webblogg/archives/001114.html
 *
 */

public class DeBruijn extends Example {

    // These parameters may be set by the user:
    //  - base 
    //  - n 
    //  - m
    int base;  // the base to use. Also known as k. 
    int n;     // number of bits representing the numbers 
    int m;     // length of the sequence, defaults to m = base^n

    // The constraint variables
    FDV[] x;        // the decimal numbers
    FDV[][] binary; // the representation of numbers in x, in the choosen base
    FDV[] bin_code; // the de Bruijn sequence (first number in binary)

    //
    // the model
    //
    
	@Override
    public void model() {

        store = new FDstore();

        int pow_base_n = pow(base, n); // base^n, the range of integers
        if (m > 0) {
            if (m > pow_base_n) {
                System.out.println("m must be <= base^n (" + m + ")");
                System.exit(1);
            }
        }

        System.out.println("Using base: " + base + " n: " + n + " m: " + m);

        // decimal representation, ranges from 0..base^n-1
        x = new FDV[m];
        for(int i = 0; i < m; i++)
            x[i] = new FDV(store, "x_" + i, 0, pow_base_n-1);
        
        //
        // convert between decimal number in x[i] and "base-ary" numbers 
        // in binary[i][0..n-1].
        //
        // (This corresponds to the predicate toNum in the MiniZinc model)
        //

        // calculate the weights array
        int[] weights = new int[n];
        int w = 1;
        for(int i = 0; i < n; i++) {
            weights[n-i-1] = w;
            w *= base;            
        }

        // connect binary <-> x
        binary = new FDV[m][n];
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                binary[i][j] = new FDV(store, "binary_" + i + "_" + j, 0, base-1);
            }
            store.impose(new SumWeight (binary[i], weights, x[i]));
        }

        //
        // assert the the deBruijn property:  element i in binary starts
        // with the end of element i-1
        //
        for(int i = 1; i < m; i++)
            for(int j = 1; j < n; j++)
                store.impose(new XeqY(binary[i-1][j], binary[i][j-1]));

        // ... "around the corner": last element is connected to the first
        for(int j = 1; j < n; j++)
            store.impose(new XeqY(binary[m-1][j], binary[0][j-1]));

        vars = new ArrayList<Variable>();
        //
        // This is the de Bruijn sequence, i.e.
        // the first element of of each row in binary[i]
        //
        bin_code = new FDV[m];
        for(int i = 0; i < m; i++) {
            bin_code[i] = new FDV(store, "bin_code_" + i, 0, base-1);
            vars.add(bin_code[i]);
            store.impose(new XeqY(bin_code[i], binary[i][0]));
        }
        
        // All values in x should be different
        store.impose(new Alldifferent(x));

        // Symmetry breaking: the minimum value in x should be the
        // first element.
        store.impose(new Min(x[0], x));

    } // end model

    /**
     * Running the program
     * java DeBruijn base n
     * java DeBruijn base n m
     * @param args between 2 and 3 arguments are used.
     */
    public static void main(String args[]) {

        int base = 2;
        int n = 4;        
        int m = 9;

        if (args.length == 3) {
            m = Integer.parseInt(args[2]);
        }
        if (args.length >= 2) {
            base = Integer.parseInt(args[0]);
            n = Integer.parseInt(args[1]);
        }

        DeBruijn debruijn = new DeBruijn();
        debruijn.base = base;
        debruijn.n = n;
        debruijn.m = m;
        
        debruijn.model();
        
        boolean result = debruijn.searchAllAtOnce();

        if (result) {

            // prints then de Bruijn sequences
            System.out.print("de Bruijn sequence:");            

            System.out.print("decimal values: ");
            for(int i = 0; i < m; i++) {
                System.out.print(debruijn.x[i].value() + " ");
            }
            System.out.println();
            
            System.out.println("\nbinary:");

            for(int i = 0; i < m; i++) {
                for(int j = 0; j < n; j++) {
                    System.out.print(debruijn.binary[i][j].value() + " ");
                }
                System.out.println(" : " + debruijn.x[i].value());
            }
            
        } else {
            System.out.println("No solutions.");
        }// end if result

    } // end main
    
    
    // integer power method
    static int pow( int x, int y) {
        int z = x; 
        for( int i = 1; i < y; i++ ) z *= x;
        return z;
    } // end pow

} // end class

 