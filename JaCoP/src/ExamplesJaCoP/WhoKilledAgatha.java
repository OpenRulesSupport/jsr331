
package ExamplesJaCoP;

import java.util.ArrayList;

import JaCoP.constraints.Eq;
import JaCoP.constraints.IfThen;
import JaCoP.constraints.Sum;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XlteqC;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestDomain;

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
* This JaCoP model was created by Hakan Kjellerstrand (hakank@bonetmail.com)
* http://www.hakank.org/JaCoP/ .
*
*	@author Hakan Kjellerstrand
*/

public class WhoKilledAgatha extends Example {


    public void model() {

        int n = 3;
        FDstore store = new FDstore();

        FDV the_killer = new FDV(store, "the_killer", 0, n-1);
        //FDV the_victim = new FDV(store, "the_victim", 0, n-1);

        int agatha  = 0;
        int butler  = 1;
        int charles = 2;

        FDV[][] hates = new FDV[n][n];
        FDV[][] richer = new FDV[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                hates[i][j]  = new FDV(store, "hates:" + i + "->" + j, 0, 1);
                richer[i][j] = new FDV(store, "richer:" + i + "->" + j, 0, 1);
            }
        }


        ArrayList<FDV> allVars; // used by the search 
        allVars = new ArrayList<FDV>();

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
        // Note: I cannot get Element to work here...
        for(int i = 0; i < n; i++) {
            store.impose(
                         new IfThen(
                                    new XeqC(the_killer, i),
                                    new XeqC(hates[i][agatha], 1)
                                    )
                         );

            store.impose(
                         new IfThen(
                                    new XeqC(the_killer, i),
                                    new XeqC(richer[i][agatha], 0)
                                    )
                         );
        }
        
        
        // define the concept of richer: 
        //   a) no one is richer than him-/herself
        for(int i = 0; i < n; i++) {
            store.impose(new XeqC(richer[i][i], 0));
        }
        
        // (contd...) 
        //   b) if i is richer than j then j is not richer than i
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if (i != j) {
                    // MiniZinc: richer[i,j] == 1 <-> richer[j,i] == 0
                     store.impose(
                                  new Eq(
                                         new XeqC(richer[i][j], 1),
                                         new XeqC(richer[j][i], 0)
                                         )
                                  );

                }
            }
        }

       
        // "Agatha hates everybody except the butler. "
        store.impose(new XeqC(hates[agatha][charles], 1));
        store.impose(new XeqC(hates[agatha][agatha], 1));
        store.impose(new XeqC(hates[agatha][butler], 0));


  
        // "Charles hates no one that Agatha hates." 
        for(int i = 0; i < n; i++) {
            // MiniZinc: hates[agatha, i] = 1 -> hates[charles, i] = 0
            store.impose(
                         new IfThen(
                                    new XeqC(hates[agatha][i], 1),
                                    new XeqC(hates[charles][i], 0)
                                    )
                         );
        }

        
        // "The butler hates everyone not richer than Aunt Agatha. "
        for(int i = 0; i < n; i++) {
            // MiniZinc: richer[i, agatha] = 0 -> hates[butler, i] = 1
             store.impose(
                          new IfThen(
                                     new XeqC(richer[i][agatha], 0),
                                     new XeqC(hates[butler][i], 1)
                                     )
                          );
        }
        
        // "The butler hates everyone whom Agatha hates." 
        for(int i = 0; i < n; i++) {
            // MiniZinc: hates[agatha, i] = 1 -> hates[butler, i] = 1
             store.impose(
                             new IfThen(
                                        new XeqC(hates[agatha][i], 1),
                                        new XeqC(hates[butler][i], 1)
                                     )
                             );

        }

        allVars.add(the_killer);

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                allVars.add(hates[i][j]);
            }
        }

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                allVars.add(richer[i][j]);
            }
        }


        // "No one hates everyone. "
        for(int i = 0; i < n; i++) {
            // MiniZinc: sum(j in r) (hates[i,j]) <= 2
            FDV a[] = new FDV[n];
            for (int j = 0; j < n; j++) {
                a[j] = new FDV(store, "a"+j, 0, 1);
                a[j] = hates[i][j];
            }
            FDV a_sum = new FDV(store, "a_sum"+i, 0, n);
            store.impose(new Sum(a, a_sum));
            store.impose(new XlteqC(a_sum, 2));
            allVars.add(a_sum);

        }
        

        // "Who killed Agatha?"
        // store.impose(eq(the_victim, agatha));


        SelectChoicePoint select = 
            new SimpleSelect (allVars.toArray(new Variable[1]),
                              new SmallestDomain(),
                              new IndomainMin ()
                              );

        Search label = new DepthFirstSearch ();
        label.getSolutionListener().searchAll(true);
        label.getSolutionListener().recordSolutions(true);
        boolean result = label.labeling(store, select);

        //
        // output
        //
        if (result) {

            int numSolutions = label.getSolutionListener().solutionsNo();
            for(int s = 1; s <= numSolutions; s++) {
                int [] res = label.getSolutionListener().getSolution(s);
                int len = res.length;

                System.out.println("the_killer: " + res[0]);

                // print the result
                for(int i = 0; i < len; i++) {
                    System.out.print(res[i] + " ");
                }
                System.out.println();

            }

            System.out.println("Number of Solutions: " + numSolutions);

        }  else {

            System.out.println("No solution.");
            
        } 

    } // end model

    /**
     * It runs the program which solves the logic puzzle "Who killed Agatha". 
     * @param args
     */
    public static void main(String args[]) {
        WhoKilledAgatha t = new WhoKilledAgatha();
        t.model();
        
    } // end main

} // end class
 
