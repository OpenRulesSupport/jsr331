package org.jcp.jsr331.hakan;

/**
 *
 * Decomposition of global constraint ExtensionalSupport in JSR-331.
 *
 * This is an decomposition of the global constraint ExtensionalSupport
 * (also called table etc).
 *
 * Model by Hakan Kjellerstrand (hakank@bonetmail.com)
 * Also see http://www.hakank.org/jsr_331/
 *
 */

import javax.constraints.*;

public class ExtensionalSupport_test1  {

  Problem p = ProblemFactory.newProblem("ExtensionalSupport_test1");

  Var[] x;
  Var[] vars;
  int n; // length of x
  int m; // size of table

  // main
  public static void main(String[] args) {
           
      ExtensionalSupport_test1 pp = new ExtensionalSupport_test1();
      pp.define();
      pp.solve();

  }
    

  // Problem definition    
  public void define() {


    //
    // data
    //
    n = 3;


    //
    // Table of valid values for x
    //
    int[][] table = {{1,2,1},
                     {1,2,2},
                     {1,3,3},
                     {2,3,3}};

    m = table.length;             

    //
    // decision variables
    //
    x = p.variableArray("x", 1, 3, n);

    // x(m) + n*m (for the tmp vars) + m for b + 1 for b_sum
    vars = new Var[n + m*n + m + 1]; 
    for(int j = 0; j < n; j++) {
      vars[j] = x[j];
    }

    //
    // constraints
    //

    // 
    // x must be the same as one of the entries in table
    //
    Var[] b = p.variableArray("b", 0, 1, m);
    // Var b_sum = p.sum(b);
    // b_sum.setName("b_sum");
    // p.post(b_sum, "=", 1);
    // p.add(b_sum);
    for(int i = 0; i < m; i++) {
      Var[] tmp = p.variableArray("tmp-"+i, 0, 1, n);
      for(int j = 0; j < n; j++) {
        System.out.print("x["+j+"] = " + table[i][j] + "   ");
        tmp[j] = p.linear(x[j],"=", table[i][j]).asBool();
        vars[n+i*n+j] = tmp[j];
        vars[n+i*n+j].setName("tmp-"+i+"-"+j);
      }
      System.out.println();
      b[i] = p.linear(p.sum(tmp),"=", n).asBool();

    }

    // Ensure that there is exactly one valid value
    Var b_sum = p.sum(b);
    b_sum.setName("b_sum");
    p.post(b_sum, "=", 1);
    p.add(b_sum);

    for(int i = 0; i < m; i++) {
      vars[n+m*n+i] = b[i];
      vars[n+m*n+i].setName("b-"+i);
    }

    vars[n+m*n+m] = b_sum;
    vars[n+m*n+m].setName("b_sum");

  }
    
    
  public void solve() {
    //
    // search
    //
    Solver solver = p.getSolver();
    SearchStrategy strategy = solver.getSearchStrategy();
    // strategy.setVars(x);
    strategy.setVars(vars);

    strategy.setVarSelectorType(VarSelectorType.INPUT_ORDER);
       
    strategy.setValueSelectorType(ValueSelectorType.MIN);
        
    //
    // tracing
    //
    // solver.traceExecution(true);

    //
    // solve
    //        
    int num_sols = 0;
    SolutionIterator iter = solver.solutionIterator();
    while (iter.hasNext()) {
      num_sols++;
      Solution s = iter.next();

      s.log();

      System.out.print("x: ");
      for(int j = 0; j < n; j++) {
        System.out.print(s.getValue("x-" + j) + " ");
      }
      System.out.println();

      System.out.print("b_sum: " + s.getValue("b_sum") + "\nb: ");
      for(int i = 0; i < m; i++) {
        System.out.print(s.getValue("b-" + i) + " ");
      }
      System.out.println();
      System.out.println("tmp:");
      for(int i = 0; i < m; i++) {
        for(int j = 0; j < n; j++) {
          System.out.print(s.getValue("tmp-" + i + "-" + j) + " ");
        }
        System.out.println();
      }
      System.out.println();
    
    }

    System.out.println("It was " + num_sols + " solutions.\n");

    solver.logStats();
  }

}
