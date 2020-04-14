//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.tests;

/* ------------------------------------------------------------
 A graph coloring problem consists of choosing colors for the nodes
 of a graph so that adjacent nodes are not the same color.
 Our goal is to offer a general way to solve a graph coloring
 problem when all the cliques of the graph are known. We'll consider only
 a very special kind of graph for this example. The kind of graph 
 that we'll color is one with n*(n-1)/2 nodes, where n is odd 
 and where every node belongs to exactly
 two maximal cliques of size n.  A clique is a complete subgraph.
 In other words, a clique is a set of nodes linked pair-wise.

 For example, for n=5, there is a graph consisting of the following
 maximal cliques:

 c0 = {0, 1, 2, 3, 4}
 c1 = {0, 5, 6, 7, 8}
 c2 = {1, 5, 9, 10, 11}
 c3 = {2, 6, 9, 12, 13}
 c4 = {3, 7, 10, 12, 14}
 c5 = {4, 8, 11, 13, 14}

 The minimum number of colors needed for this graph is n since
 there is a click of size n.  Consequently, our problem is to find
 whether there is a way to color such a graph in n colors.

 */

import java.util.Date;

import javax.constraints.Constraint;
import javax.constraints.ProblemFactory;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;
import javax.constraints.Problem;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.junit.Assert;

public class TestGraphColoring extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestGraphColoring.class));
	}
	
	static int f(int i, int j, int n) {
		if (j >= i)
			return (i * n - i * (i + 1) / 2 + j - i);
		else
			return f(j, i - 1, n);
	}
	
	public void testExecute() {
		try {
			Problem problem = ProblemFactory.newProblem("Graph Coloring");
			int clique_size = 5; //31;
			problem.log("Graph Coloring for " + clique_size
					+ " cliques. " + new Date());
			int n = (clique_size % 2 > 0) ? clique_size + 1 : clique_size;
			boolean redundant_constraint = true;
			int size = n * (n - 1) / 2;
			int i, j;
			int nbColors = n - 1;

			Var[] vars = problem.variableArray("vars",0, nbColors - 1, size);

			Var[][] cliques = new Var[n][n - 1];
			problem.log("Post AllDifferent constraints");
			for (i = 0; i < n; i++) {
				for (j = 0; j < n - 1; j++) {
					int node = f(i, j, n);
					Var v = vars[node];
					cliques[i][j] = v;
				}
				
				problem.postAllDifferent(cliques[i]);
			}

			// Redundant Constraint: every color is used at least n/2 times
			int[] colAr = new int[nbColors];
			for (int k = 0; k < nbColors; k++)
				colAr[k] = k;
			problem.log("Post Redundant Constraint (GCC)");
			Var[] cards = problem.variableArray("cards", 0, nbColors-1, nbColors);
			problem.postGlobalCardinality(vars,colAr,cards); 
			if (redundant_constraint) {
				int min_color_use = n / 2;
				for (int v = 0; v < cards.length; v++) {
					Var card = cards[v];
					problem.post(card,">=",min_color_use);
				}
			}

//			========= Problem Resolution ==================
			Solver solver = problem.getSolver();
			SearchStrategy strategy = solver.getSearchStrategy();
			strategy.setVars(vars);
			strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
			Solution solution = solver.findSolution();
			assertNotNull(solution);
//			for(i = 0; i < cards.length; i++){
//				assertEquals(solution.getValue("cards-"+i),16);
//			}
			
			int[] expectedLastClique5 =	{ 4, 8, 11, 13, 14 };
			int[] expectedLastClique31 =
			  { 30,29,28,27,25,17,18,14,3,19,13,16,15,22,7,20,2,9,23,24,4,10,26,12,8,11,6,0,5,21,1 };
			int[] actualLastClique = new int[n-1];
			
			for (i = 0; i < n; i++) {
				problem.log("\nClique " + i + ":");
				StringBuffer str = new StringBuffer();
				for (j = 0; j < n - 1; j++) {
					int node = f(i, j, n);
					int color = solution.getValue("vars-"+node);
					if (i==n-1)
						actualLastClique[j] = color;
					str.append(" " + node + "=" + color);
//					str.append(", " + color);
				}
				problem.log(str.toString());
			}
			
			if (n==5)
				Assert.assertArrayEquals(expectedLastClique5, actualLastClique);
			if (n==31)
				Assert.assertArrayEquals(expectedLastClique31, actualLastClique);

		} catch (Exception e) {
			System.out.println("failure: " + e);
		}
	}
}
