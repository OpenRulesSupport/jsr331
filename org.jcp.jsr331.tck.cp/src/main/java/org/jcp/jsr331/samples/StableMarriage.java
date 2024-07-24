package org.jcp.jsr331.samples;

/**
  *
  * Stable marriage problem in JSR-331.
  *
  * Model by Hakan Kjellerstrand (hakank@bonetmail.com)
  * Also see http://www.hakank.org/jsr_331/
  *
  */

import javax.constraints.*;

public class StableMarriage {

  Problem p = ProblemFactory.newProblem("StableMarriage");
  int n;
  
 // Data from https://dmcommunity.org/challenge/challenge-june-2024/
static int[][][] dmcommunity = {
    // rankWomen
   {{5, 1, 2, 4, 3},
    {4, 1, 3, 2, 5},
    {5, 3, 2, 4, 1},
    {1, 5, 4, 3, 2},
    {4, 3, 2, 1, 5}},

   // rankMen
   {{1, 2, 4, 3, 5},
    {3, 5, 1, 2, 4},
    {5, 4, 2, 1, 3},
    {1, 4, 3, 2, 5},
    {4, 2, 3, 5, 1}}
  };

// Data from Pascal Van Hentenryck's OPL book
static int[][][] van_hentenryck = {
  // rankWomen
 {{1, 2, 4, 3, 5},
  {3, 5, 1, 2, 4},
  {5, 4, 2, 1, 3},
  {1, 3, 5, 4, 2},
  {4, 2, 3, 5, 1}},

 // rankMen
 {{5, 1, 2, 4, 3},
  {4, 1, 3, 2, 5},
  {5, 3, 2, 4, 1},
  {1, 5, 4, 3, 2},
  {4, 3, 2, 1, 5}}
};

// Data from MathWorld http://mathworld.wolfram.com/StableMarriageProblem.html
static int[][][]  mathworld = {
  // rankWomen
  {{3, 1, 5, 2, 8, 7, 6, 9, 4},
   {9, 4, 8, 1, 7, 6, 3, 2, 5},
   {3, 1, 8, 9, 5, 4, 2, 6, 7},
   {8, 7, 5, 3, 2, 6, 4, 9, 1},
   {6, 9, 2, 5, 1, 4, 7, 3, 8},
   {2, 4, 5, 1, 6, 8, 3, 9, 7},
   {9, 3, 8, 2, 7, 5, 4, 6, 1},
   {6, 3, 2, 1, 8, 4, 5, 9, 7},
   {8, 2, 6, 4, 9, 1, 3, 7, 5}},

  // rankMen
  {{7, 3, 8, 9, 6, 4, 2, 1, 5},
   {5, 4, 8, 3, 1, 2, 6, 7, 9},
   {4, 8, 3, 9, 7, 5, 6, 1, 2},
   {9, 7, 4, 2, 5, 8, 3, 1, 6},
   {2, 6, 4, 9, 8, 7, 5, 1, 3},
   {2, 7, 8, 6, 5, 3, 4, 1, 9},
   {1, 6, 2, 3, 8, 5, 4, 9, 7},
   {5, 6, 9, 1, 2, 8, 4, 3, 7},
   {6, 1, 4, 7, 5, 8, 3, 9, 2}}};

// Data from http://www.csee.wvu.edu/~ksmani/courses/fa01/random/lecnotes/lecture5.pdf
static int[][][] problem3 = {
  // rankWomen
  {{1,2,3,4},
   {4,3,2,1},
   {1,2,3,4},
   {3,4,1,2}},

  // rankMen"
  {{1,2,3,4},
   {2,1,3,4},
   {1,4,3,2},
   {4,3,1,2}}};

// Data from http://www.comp.rgu.ac.uk/staff/ha/ZCSP/additional_problems/stable_marriage/stable_marriage.pdf
static int[][][] problem4 = {
  // rankWomen
  {{1,5,4,6,2,3},
   {4,1,5,2,6,3},
   {6,4,2,1,5,3},
   {1,5,2,4,3,6},
   {4,2,1,5,6,3},
   {2,6,3,5,1,4}},

  // rankMen
  {{1,4,2,5,6,3},
   {3,4,6,1,5,2},
   {1,6,4,2,3,5},
   {6,5,3,4,2,1},
   {3,1,2,4,5,6},
   {2,3,1,6,5,4}}};


    // Main
    public static void main(String[] args) {
        
        StableMarriage pp = new StableMarriage();
        
        pp.define(dmcommunity, "DMCommunity Challenge");
        pp.solve();
        
        pp.define(van_hentenryck, "Van Hentenryck");
        pp.solve();
    
        pp = new StableMarriage();
        pp.define(mathworld, "MathWorld");
        pp.solve();
    
        pp = new StableMarriage();
        pp.define(problem3, "Problem 3");
        pp.solve();
    
        pp = new StableMarriage();
        pp.define(problem4, "Problem 4");
        pp.solve();

    }
    

  // Problem definition    
  public void define(int[][][] ranks, String problem_name) {

    System.out.println("\n####### Problem: " + problem_name);

    int[][] rankWomen = ranks[0];
    int[][] rankMen   = ranks[1];

    n = rankWomen.length;

    //
    // variables
    // 
    Var[] wife = p.variableArray("wife", 0, n-1, n);
    Var[] husband = p.variableArray("husband", 0, n-1, n);

    //
    // constraints
    // 
    for(int m = 0; m < n; m++) {
      p.postElement(husband, wife[m], "=", m);
    }

    for(int w = 0; w < n; w++) {
      p.postElement(wife, husband[w], "=", w);
    }

    for(int m = 0; m < n; m++) {
      for(int o = 0; o < n; o++) {
        Constraint b1 = p.linear(p.element(rankMen[m], wife[m]), ">", rankMen[m][o]);
        Constraint b2 = p.linear(p.element(rankWomen[o], husband[o]), "<", rankWomen[o][m]);
        b1.implies(b2).post();
      }
    }

    for(int w = 0; w < n; w++) {
      for(int o = 0; o < n; o++) {
        Constraint b1 = p.linear(p.element(rankWomen[w], husband[w]), ">", rankWomen[w][o]);
        Constraint b2 = p.linear(p.element(rankMen[o], wife[o]), "<", rankMen[o][w]);
        b1.implies(b2).post();
      }
    }

  }
    
  // Problem resolution  
  public void solve() {
    Solver solver = p.getSolver();
        
    Solution s1 = solver.findSolution(ProblemState.RESTORE);
    if (s1 == null) {
      System.out.println("No solution.");
      return;
    } else {
      System.out.println("\nFirst solution:\n");
      System.out.print("wife   : ");
      for(int i = 0; i < n; i++) {
        System.out.print(s1.getValue("wife-"+i) + " ");
      }
      System.out.print("\nhusband: ");
      for(int i = 0; i < n; i++) {
        System.out.print(s1.getValue("husband-"+i) + " ");
      }
    }

    System.out.println("\n\nAll solutions:");
    int num_sols = 0;
    SolutionIterator iter = solver.solutionIterator();
    while (iter.hasNext()) {
      num_sols++;
      Solution s = iter.next();
      System.out.println("Solution #" + num_sols);
      System.out.print("wife   : ");
      for(int i = 0; i < n; i++) {
        System.out.print(s.getValue("wife-"+i) + " ");
      }
      System.out.print("\nhusband: ");
      for(int i = 0; i < n; i++) {
        System.out.print(s.getValue("husband-"+i) + " ");
      }
      System.out.println("\n");
    }
    //System.out.println("There were " + num_sols + " solutions\n");

    solver.logStats();
  }

}
