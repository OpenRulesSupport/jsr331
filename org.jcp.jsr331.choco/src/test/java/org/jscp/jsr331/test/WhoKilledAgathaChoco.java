package org.jscp.jsr331.test;
/**
  *
  *   Who killed agatha? (The Dreadsbury Mansion Murder Mystery) in Choco.

  *   This is a standard benchmark for theorem proving.  
  *   http://www.lsv.ens-cachan.fr/~goubault/H1.dist/H1.1/Doc/h1003.html
  *   """ 
  *   Someone in Dreadsbury Mansion killed Aunt Agatha. 
  *   Agatha, the butler, and Charles live in Dreadsbury Mansion, and 
  *   are the only ones to live there. A killer always hates, and is no 
  *   richer than his victim. Charles hates noone that Agatha hates. Agatha 
  *   hates everybody except the butler. The butler hates everyone not richer 
  *   than Aunt Agatha. The butler hates everyone whom Agatha hates. 
  *   Noone hates everyone. Who killed Agatha? 
  *   """
  
  *   Originally from 
  *   F. J. Pelletier: Seventy-five problems for testing automatic theorem provers. Journal of Automated Reasoning, 2: 191â€“216, 1986.
  
  *   Compare with the following models:
  *   - MiniZinc: http://www.hakank.org/minizinc/who_killed_agatha.mzn
  *   - Comet: http://www.hakank.org/comet/who_killed_agatha.mzn
  *   - Gecode: http://www.hakank.org/gecode/who_killed_agatha.cpp

  * 
  * This Choco model was created by Hakan Kjellerstrand (hakank@bonetmail.com)
  * Also, see my Choco page: http://www.hakank.org/choco/ 
  *
  */

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.*;
import choco.cp.solver.*;
import choco.kernel.model.variables.integer.*;
import choco.kernel.*;
import choco.kernel.model.*;
import choco.kernel.model.variables.*;
import choco.kernel.model.constraints.*;


import java.io.*;
import java.util.*;
import java.text.*;

/*
  
  This version use the nth constraint (Element) and accordingly 
  transposed hates/richer matrices.

 */

public class WhoKilledAgathaChoco {


    public void model() {

        int n = 3;
        CPModel m = new CPModel();

        IntegerVariable the_killer = makeIntVar("the_killer", 0, n-1);
        //IntegerVariable the_victim = makeIntVar("the_victim", 0, n-1);

        int agatha  = 0;
        int butler  = 1;
        int charles = 2;

        // constants for nth
        IntegerVariable zero = makeIntVar("zero", 0, 0); 
        IntegerVariable one = makeIntVar("one", 1, 1); 

        IntegerVariable[][] hates = new IntegerVariable[n][n];
        IntegerVariable[][] richer = new IntegerVariable[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                hates[i][j]  = makeIntVar("hates" + i + "" + j, 0, 1);
                richer[i][j] = makeIntVar("richer" + i + "" + j, 0, 1);
            }
        }

        //
        // The comments below contains the corresponding MiniZinc code,
        // for documentation and comparision.
        //
        
        // """
        // Agatha, the butler, and Charles live in Dreadsbury Mansion, and 
        // are the only ones to live there. 
        // """


        // "A killer always hates, and is no richer than his victim."
        // MiniZinc: hates[the_killer, the_victim] = 1
        //           richer[the_killer, the_victim] = 0
        // Note: I cannot get nth to work here...
        for(int i = 0; i < n; i++) {
            m.addConstraint(nth(the_killer, hates[agatha], one));
            m.addConstraint(nth(the_killer, richer[agatha], zero));
        }
        
        
        // define the concept of richer: 
        //   a) no one is richer than him-/herself
        for(int i = 0; i < n; i++) {
            m.addConstraint(eq(richer[i][i], 0));
        }
        
        // (contd...) 
        //   b) if i is richer than j then j is not richer than i
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if (i != j) {
                    // MiniZinc: richer[i,j] == 1 <-> richer[j,i] == 0
                     m.addConstraint(
                                     ifOnlyIf(
                                            eq(richer[j][i], 1),
                                            eq(richer[i][j], 0)
                                              )
                                     );
                }
            }
        }
  
        // "Charles hates no one that Agatha hates." 
        for(int i = 0; i < n; i++) {
            // MiniZinc: hates[agatha, i] = 1 -> hates[charles, i] = 0
            m.addConstraint(
                            implies(
                                    eq(hates[i][agatha], 1),
                                    eq(hates[i][charles], 0)
                                    )
                            );
        }
        
        // "Agatha hates everybody except the butler. "
        m.addConstraint(eq(hates[charles][agatha], 1));
        m.addConstraint(eq(hates[agatha][agatha], 1));
        m.addConstraint(eq(hates[butler][agatha], 0));
        
        // "The butler hates everyone not richer than Aunt Agatha. "
        for(int i = 0; i < n; i++) {
            // MiniZinc: richer[i, agatha] = 0 -> hates[butler, i] = 1
             m.addConstraint(
                             implies(
                                     eq(richer[agatha][i], 0),
                                     eq(hates[i][butler], 1)
                                     )
                             );
        }
        
        // "The butler hates everyone whom Agatha hates." 
        for(int i = 0; i < n; i++) {
            // MiniZinc: hates[agatha, i] = 1 -> hates[butler, i] = 1
             m.addConstraint(
                             implies(
                                        eq(hates[i][agatha], 1),
                                        eq(hates[i][butler], 1)
                                     )
                             );

        }

        // "No one hates everyone. "
        for(int i = 0; i < n; i++) {
            // MiniZinc: sum(j in r) (hates[i,j]) <= 2
            IntegerVariable a[] = makeIntVarArray("a", n, 0,1);
            for (int j = 0; j < n; j++) {
                a[j] = hates[j][i];
            }
            m.addConstraint(leq(sum(a), 2));

        }
        
        // "Who killed Agatha?"
        // m.addConstraint(eq(the_victim, agatha));
        
        
        CPSolver S = new CPSolver();
        S.read(m);

        S.monitorTimeLimit(true);
        S.monitorBackTrackLimit(true);
        S.monitorNodeLimit(true);
        S.monitorFailLimit(true);

        S.solve();

        if(S.isFeasible()) {
            
            int num_solutions = 0;
            do {

                
                System.out.print("the_killer: " + S.getVar(the_killer).getVal() + " ");
                // System.out.print("the_victim: " + S.getVar(the_victim).getVal() + " ");
                /*
                System.out.println("\n" + "hates:" );
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        System.out.print(S.getVar(hates[i][j]).getVal() + " ");
                    }
                System.out.println();                
                }

                System.out.println("\n" + "richer:" );
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        System.out.print(S.getVar(richer[i][j]).getVal() + " ");
                    }
                System.out.println();                
                }
                */
                System.out.println();                
                num_solutions++;

            } while (S.nextSolution() == Boolean.TRUE);

            System.out.println("Number of solutions: " + num_solutions);

        } else {

            System.out.println("Problem is not feasible.");

        }

//        S.printRuntimeSatistics(); // sic!

    } // end model

    public static void main(String args[]) {
        WhoKilledAgathaChoco t = new WhoKilledAgathaChoco();
        t.model();
        
    } // end main

} // end class
 
