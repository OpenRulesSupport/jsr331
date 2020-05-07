package org.jcp.jsr331.samples;

/* ------------------------------------------------------------
 A graph coloring problem consists of choosing colors for the nodes
 of a graph so that adjacent nodes are not the same color.
 Our goal is to offer a general way to solve a graph coloring
 problem when all the cliques of the graph are known. We'll consider only
 a very special kind of graph for this example. The kind of graph 
 that we'll color is one with n*(n-1)/2 nodes, where n is odd 
 and where every node belongs to exactly two maximal cliques of size n.
 A clique is a complete subgraph. In other words, a clique is a set 
 of nodes linked pair-wise.

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

import javax.constraints.*;

public class GraphColoring {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");
	
	int n;
	Var[] vars;
	Var[] cards;
	
	public GraphColoring(int cliqueSize) {
		n = (cliqueSize % 2 > 0) ? cliqueSize + 1 : cliqueSize;
		p.log("Graph Coloring for " + cliqueSize + " cliques. " + new Date());
	}
	
	int f(int i, int j) {
		if (j >= i)
			return (i * n - i * (i + 1) / 2 + j - i);
		else
			return f(j, i - 1);
	}

	public void define() {
		try {
			boolean redundant_constraint = true;
			int size = n * (n - 1) / 2;
			int i, j;
			int nbColors = n - 1;

			vars = p.variableArray("vars",0, nbColors - 1, size);

			Var[][] cliques = new Var[n][n - 1];
			p.log("Post AllDifferent constraints");
			for (i = 0; i < n; i++) {
				for (j = 0; j < n - 1; j++) {
					int node = f(i, j);
					Var v = vars[node];
					cliques[i][j] = v;
				}
				
				p.postAllDifferent(cliques[i]);
			}

			// Redundant ConstraintClass: every color is used at least n/2 times
			int[] colAr = new int[nbColors];
			for (int k = 0; k < nbColors; k++)
				colAr[k] = k;
			p.log("Post Redundant ConstraintClass (GCC)");
			cards = p.variableArray("cards", 0, nbColors-1, nbColors);
			p.postGlobalCardinality(vars,colAr,cards); 
			if (redundant_constraint) {
				int min_color_use = n / 2;
				for (int v = 0; v < cards.length; v++) {
					Var card = cards[v];
					p.post(card,">=",min_color_use);
				}
			}

		} catch (Exception e) {
			p.log("failure: " + e);
		}
	}
	
	public void solve() {
			Solver solver = p.getSolver();
			SearchStrategy strategy = solver.getSearchStrategy();
			strategy.setVars(vars);
			strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
			Solution solution = solver.findSolution();
			if(solution == null)
				p.log("no solution found");
			else{
				p.log("Solution:");
				StringBuffer s = new StringBuffer();
				for(int l = 0; l < cards.length; l++){
					s.append(" " + cards[l].getValue());
				}
				p.log("cards: " + s.toString());

				for (int i = 0; i < n; i++) {
					p.log("\nClique " + i + ": (node=color)");
					StringBuffer str = new StringBuffer();
					for (int j = 0; j < n - 1; j++) {
						int node = f(i, j);
						int color = solution.getValue("vars-"+node);
						str.append(" " + node + "=" + color);
					}
					p.log(str.toString());
				}
			}
			solver.logStats();
	}
	
	public static void main(String[] args) {
		String arg = (args.length == 0) ? "31" : args[0];
		int cliqueSize = Integer.parseInt(arg);
		GraphColoring t = new GraphColoring(cliqueSize);
		t.define();
		t.solve();
	}
}
