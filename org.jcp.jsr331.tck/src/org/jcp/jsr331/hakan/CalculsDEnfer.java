package org.jcp.jsr331.hakan;

/**
 *
 * Calculs d'enfer puzzle in JSR-331.
 *  
 * Problem from Jianyang Zhou "The Manual of NCL version 1.2", page 33
 * http://citeseer.ist.psu.edu/161721.html
 *
 * The solution is the manual is:
 * '''
 * a = -16, b = -14, c = -13, d = -12, e = -10,
 * f = 4, g = 13, h = -1, i = -3, j = -11, k = -9,
 * l = 16, m = -8, n = 11, o = 0, p = -6, q = -4,
 * r = 15, s = 2, t = 9, u = -15, v = 14, w = -7,
 * x = 7, y = -2, z = -5.
 * 
 * max_{#1\in [1,26]}{|x_{#1}|} minimized to 16
 * '''
 * 
 * See the discussion of the Z model:
 * http://www.comp.rgu.ac.uk/staff/ha/ZCSP/additional_problems/calculs_enfer/calculs_enfer.ps
 * (which shows the same solution).
 * 
 * Also, compare with the following models:
 * Comet   : http://www.hakank.org/comet/calculs_d_enfer.co
 * ECLiPSE : http://www.hakank.org/eclipse/calculs_d_enfer.ecl
 * Tailor/Essence': http://www.hakank.org/tailor/calculs_d_enfer.eprime
 * Gecode  : http://www.hakank.org/gecode/calculs_d_enfer.cpp
 * MiniZinc: http://www.hakank.org/minizinc/calculs_d_enfer.mzn
 * SICStus: http://hakank.org/sicstus/calculs_d_enfer.pl
 * Google CP Solver:  http://hakank.org/google_or_tools/calculs_d_enfer.py
 *
 *
 * Model by Hakan Kjellerstrand (hakank@bonetmail.com)
 * Also see http://www.hakank.org/jsr_331/
 *
 */

import javax.constraints.*;

public class CalculsDEnfer {

    Var[] A;
    Var A_max;
    Problem problem = ProblemFactory.newProblem("Calculs d'Enfer");

    // main
    public static void main(String[] args) {

        CalculsDEnfer prob = new CalculsDEnfer();
        prob.define();
        prob.solve();

    }
    

    // Problem definition    
    public void define() {

        int nn = 26;

        problem.setDomainType(DomainType.DOMAIN_SPARSE);
        int dom_min = -100;
        int dom_max = 100;

        A_max = problem.variable("A_max", 0, dom_max);
        Var a = problem.variable("a", dom_min, dom_max);
        Var b = problem.variable("b", dom_min, dom_max);
        Var c = problem.variable("c", dom_min, dom_max);
        Var d = problem.variable("d", dom_min, dom_max);
        Var e = problem.variable("e", dom_min, dom_max);
        Var f = problem.variable("f", dom_min, dom_max);
        Var g = problem.variable("g", dom_min, dom_max);
        Var h = problem.variable("h", dom_min, dom_max);
        Var i = problem.variable("i", dom_min, dom_max);
        Var j = problem.variable("j", dom_min, dom_max);
        Var k = problem.variable("k", dom_min, dom_max);
        Var l = problem.variable("l", dom_min, dom_max);
        Var m = problem.variable("m", dom_min, dom_max);
        Var n = problem.variable("n", dom_min, dom_max);
        Var o = problem.variable("o", dom_min, dom_max);
        Var p = problem.variable("p", dom_min, dom_max);
        Var q = problem.variable("q", dom_min, dom_max);
        Var r = problem.variable("r", dom_min, dom_max);
        Var s = problem.variable("s", dom_min, dom_max);
        Var t = problem.variable("t", dom_min, dom_max);
        Var u = problem.variable("u", dom_min, dom_max);
        Var v = problem.variable("v", dom_min, dom_max);
        Var w = problem.variable("w", dom_min, dom_max);
        Var x = problem.variable("x", dom_min, dom_max);
        Var y = problem.variable("y", dom_min, dom_max);
        Var z = problem.variable("z", dom_min, dom_max);

        Var[] _A = {a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z};        
        A = _A;

        Var[] A_abs = new Var[nn];
        for(int ii = 0; ii < nn; ii++) {
            A_abs[ii] = A[ii].abs();
        }

        problem.log("Before Constraint Posting" + problem.getVars());

        problem.postMax(A_abs,"=", A_max);
        problem.postAllDifferent(A);

        problem.post(z.plus(e).plus(r).plus(o) , "=",  0);
        problem.post(o.plus(n).plus(e) , "=",  1);
        problem.post(t.plus(w).plus(o) , "=",  2);
        problem.post(t.plus(h).plus(r).plus(e).plus(e) , "=",  3);
        problem.post(f.plus(o).plus(u).plus(r) , "=",  4);
        problem.post(f.plus(i).plus(v).plus(e) , "=",  5);
        problem.post(s.plus(i).plus(x) , "=",  6);
        problem.post(s.plus(e).plus(v).plus(e).plus(n) , "=",  7);
        problem.post(e.plus(i).plus(g).plus(h).plus(t) , "=",  8);
        problem.post(n.plus(i).plus(n).plus(e) , "=",  9);
        problem.post(t.plus(e).plus(n) , "=",  10);
        problem.post(e.plus(l).plus(e).plus(v).plus(e).plus(n) , "=",  11);
        problem.post(t.plus(w).plus(e).plus(l).plus(f) , "=",  12);

        problem.log("After Constraint Posting" + problem.getVars());
    }
    
    
    public void solve() {
        //
        // search
        //
        Solver solver = problem.getSolver();
        SearchStrategy strategy = solver.getSearchStrategy();
        // strategy.setVars(A);

        Var[] vars = new Var[A.length+1];
        for(int I = 0; I < A.length; I++) {
          vars[I] = A[I];
        }
        vars[A.length] = A_max;
        strategy.setVars(vars);

        // strategy.setVarSelectorType(VarSelectorType.INPUT_ORDER);
        // strategy.setVarSelectorType(VarSelectorType.MIN_VALUE);
        // strategy.setVarSelectorType(VarSelectorType.MAX_VALUE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_MIN_VALUE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_RANDOM);
        // strategy.setVarSelectorType(VarSelectorType.RANDOM);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_MAX_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_OVER_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_OVER_WEIGHTED_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MAX_WEIGHTED_DEGREE);
        strategy.setVarSelectorType(VarSelectorType.MAX_IMPACT);
        // strategy.setVarSelectorType(VarSelectorType.MAX_DEGREE);
        // strategy.setVarSelectorType(VarSelectorType.MAX_REGRET);
        
        
        
        
        // strategy.setValueSelectorType(ValueSelectorType.IN_DOMAIN);
        // strategy.setValueSelectorType(ValueSelectorType.MIN);
        strategy.setValueSelectorType(ValueSelectorType.MAX);
        // strategy.setValueSelectorType(ValueSelectorType.MIN_MAX_ALTERNATE);
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
        /*
        Solution solution = problem.getSolver().findOptimalSolution(problem.getVar("A_max"));
        SolutionIterator iter = solver.solutionIterator();
        solution.log();
        System.out.println();
        */

        SolutionIterator iter = solver.solutionIterator();
        int bestValue = Integer.MAX_VALUE;
        Solution solution = null;
        int max_solutions = 20;
        int num_solutions = 0;
        while(iter.hasNext()) {
            solution = iter.next();
            solution.log();
            try {
                int newValue = solution.getValue("A_max");
                if (bestValue > newValue) {
                    bestValue = newValue;
                    System.out.println("Got " + bestValue);
                }
                problem.post(problem.getVar("A_max"),"<",newValue); // may fail
            } catch (Exception e) {
              System.out.println(e);
              break;
            }

            num_solutions++;
            if (num_solutions >= max_solutions) {
              System.out.println("Max number of solutions reached...");
              break;
            }

        }

        solver.logStats();
    }

}
