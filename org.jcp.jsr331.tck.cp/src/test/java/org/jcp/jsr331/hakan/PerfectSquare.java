package org.jcp.jsr331.hakan;

/**
 *
 * Perfect square problem in JSR-331.
 *
 * Model by Hakan Kjellerstrand (hakank@bonetmail.com)
 * Also see http://www.hakank.org/jsr_331/
 *
 */

import javax.constraints.*;

import java.util.*;

public class PerfectSquare {

    int base;
    int num_sides;
    int[] sides;
    Problem p = ProblemFactory.newProblem("PerfectSquare");

    // main
    public static void main(String[] args) {

        PerfectSquare perfectSquare = new PerfectSquare();
        perfectSquare.define();
        perfectSquare.solve();

    }
    
    /**
     *
     * Experimental decomposition of cumulative.
     *
     * Inspired by the MiniZinc implementation:
     * http://www.g12.csse.unimelb.edu.au/wiki/doku.php?id=g12:zinc:lib:minizinc:std:cumulative.mzn&s[]=cumulative
     * The MiniZinc decomposition is discussed in the paper:
     * A. Schutt, T. Feydy, P.J. Stuckey, and M. G. Wallace.
     * 'Why cumulative decomposition is not as bad as it sounds.'
     * Download:
     * http://www.cs.mu.oz.au/%7Epjs/rcpsp/papers/cp09-cu.pdf
     * http://www.cs.mu.oz.au/%7Epjs/rcpsp/cumu_lazyfd.pdf
     *
     * Parameters:
     *   s: start_times    assumption: array of IntVar
     *   d: durations      assumption: array of int
     *   r: resources      assumption: array of int
     *   b: resource limit assumption: IntVar or int
     */
    public void my_cumulative(Var[] s, int[] d, int[] r, int b) {
        int n = s.length;

        int times_min = 999999;
        int times_max = 0;
        for(int i = 0; i < n; i++) {
            int s_min = s[i].getMin();
            int s_max = s[i].getMax();
            
            if (s_max > times_max) {
                times_max = s_max;
            }
            if (s_min < times_min) {
                times_min = s_min;
            }
        }

        int d_max = 0;
        for(int i = 0; i < n; i++) {
            if (d[i] > d_max) {
                d_max = d[i];
            }
        }        
        times_max = times_max + d_max;
        
        for(int t = times_min; t <= times_max; t++) {
            ArrayList<Var> bb = new ArrayList<Var>();
            for(int i = 0; i < n; i++) {
                Var c1 = p.linear(s[i], "<=", t).asBool();
                Var c2 = p.linear(s[i].plus(d[i]), ">", t).asBool();
                bb.add(c1.multiply(c2).multiply(r[i]));
            }
            p.post(p.sum(bb.toArray(new Var[1])), "<=", b);
        }

        // sanity check: ensure that b < sum(r)
        /*
        int sum_r = 0;
        for(int i = 0; i < n; i++) {
            sum_r += r[i];
        }
        post(b, "<=", sum_r);
        */

    }


    // Problem definition    
    public void define() {

        // simple problem
        base = 4;
        num_sides = 4;
        int[] _sides = {2,2,2,2};
  
        // not so simple
        /*
        base = 14;
        num_sides = 12;
        int[] _sides = {1,1,1,1,2,3,3,3,5,6,6,8};
        */

        /*
        base = 30;
        num_sides = 19;
        int[] _sides = {1,1,1,1,2,2,3,3,4,5,7,8,8,9,9,10,10,11,13};
        */

        sides = _sides;

        for(int i = 0; i < num_sides; i++) {
            System.out.print(sides[i] + " ");
        }
        System.out.println();

        //
        // reverse array
        //
        if (sides[0] > sides[num_sides-1]) {
            System.out.print("Reverse the array");
            int[] rev = new int[num_sides];
            int j=0;
            for (int i= sides.length-1; i >=0; i--) {
                rev[j++] = sides[i];
            }
            sides = rev;
        }
        for(int i = 0; i < num_sides; i++) {
            System.out.print(sides[i] + " ");
        }
        System.out.println();

        // variables
        Var[] x = p.variableArray("x", 1, base, num_sides);
        Var[] y = p.variableArray("y", 1, base, num_sides);

        for(int i = 0; i < num_sides; i++) {
            p.post(x[i].plus(sides[i]), "<=", base + 1);
            p.post(y[i].plus(sides[i]), "<=", base + 1);
        }

        
        // non overlap
        /*
          s.x + s.size <= t.x \/ 
          t.x + s.size <= s.x \/ 
          s.y + s.size <= t.y \/ 
          t.y + s.size <= s.y;
        */
        for(int i = 0; i < num_sides; i++) {
            for(int j = 0; j < num_sides; j++) {
                if (i < j) {
                    Var c1 = p.linear(x[i].plus(sides[i]), "<=", x[j]).asBool();
                    Var c2 = p.linear(x[j].plus(sides[j]), "<=", x[i]).asBool();
                    Var c3 = p.linear(y[i].plus(sides[i]), "<=", y[j]).asBool();
                    Var c4 = p.linear(y[j].plus(sides[j]), "<=", y[i]).asBool();
                    p.post(c1.plus(c2).plus(c3).plus(c4), ">=", 1);
                }
            }
        }

        // and then the experimental cumulative
        /*
        my_cumulative(x, sides, sides, base);
        my_cumulative(y, sides, sides, base);
        */


    }
    
    
    public void solve() {
        //
        // search
        //
        Solver solver = p.getSolver();
        SearchStrategy strategy = solver.getSearchStrategy();

        // strategy.setVarSelectorType(VarSelectorType.INPUT_ORDER);
        strategy.setVarSelectorType(VarSelectorType.MIN_VALUE);
        // strategy.setVarSelectorType(VarSelectorType.MAX_VALUE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_MIN_VALUE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_RANDOM);
        // strategy.setVarSelectorType(VarSelectorType.RANDOM);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_MAX_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_OVER_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_OVER_WEIGHTED_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MAX_WEIGHTED_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MAX_IMPACT);
        // strategy.setVarSelectorType(VarSelectorType.MAX_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MAX_REGRET);
        
        
        
        
        // strategy.setValueSelectorType(ValueSelectorType.IN_DOMAIN);
        // strategy.setValueSelectorType(ValueSelectorType.MIN);
        // strategy.setValueSelectorType(ValueSelectorType.MAX);
        strategy.setValueSelectorType(ValueSelectorType.MIN_MAX_ALTERNATE);
        // strategy.setValueSelectorType(ValueSelectorType.MIDDLE);
        // strategy.setValueSelectorType(ValueSelectorType.MEDIAN);
        // strategy.setValueSelectorType(ValueSelectorType.RANDOM);
        // strategy.setValueSelectorType(ValueSelectorType.MIN_IMPACT);
        // strategy.setValueSelectorType(ValueSelectorType.CUSTOM);
        
        //
        // tracing
        //
        // solver.addSearchStrategy(new StrategyLogVariables(solver)); 
        // solver.traceExecution(true);

        //
        // solve
        //        
        int num_sols = 0;
        SolutionIterator iter = solver.solutionIterator();
        while (iter.hasNext()) {
            num_sols++;
            Solution s = iter.next();
            System.out.println();

            // reset square
            int[][] res = new int[base][base];
            for(int i = 0; i < base; i++) {
                for(int j = 0; j < base; j++) {
                    res[i][j] = -1;
                }
            }

            for(int ix = 0; ix < num_sides; ix++) {
                int xix = s.getValue("x-"+ix)-1;
                int yix = s.getValue("y-"+ix)-1;
                // System.out.println("ix: " + ix + " xix " + xix + " yix: " + yix);
                for(int j = xix; j < xix + sides[ix]; j++) {
                    for(int k = yix; k < yix + sides[ix]; k++) {
                        // System.out.println("j: " + j + " k: " + k);
                        if (res[j][k] > -1) {
                            System.out.println("\tWrong!: this cell is" + res[j][k]);
                        } 
                        res[j][k] = ix;

                    }
                }
            }

            for(int i = 0; i < base; i++) {
                for(int j = 0; j < base; j++) {
                    System.out.format("%3s", (res[i][j]+1) + " ");
                }
                System.out.println();
            }
            System.out.println();

            if (num_sols > 1) {
                break;
            }

        }

        solver.logStats();
    }

}
