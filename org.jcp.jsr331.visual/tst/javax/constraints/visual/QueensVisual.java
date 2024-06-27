package javax.constraints.visual;

/* ------------------------------------------------------------
 The eight-queens problem is a well known problem that involves
 placing eight queens on a chess board in such a way that none
 of them can capture any other using the conventional moves
 allowed to a queen.  In other words, the problem is to select
 eight squares on a chess board so that any pair of selected
 squares is never aligned vertically, horizontally, nor
 diagonally.
 ------------------------------------------------------------ */
import ie.ucc.cccc.viz.Viz;

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.ValueSelector.ValueSelectorType;
import javax.constraints.VarSelector.VarSelectorType;
import javax.constraints.impl.constraint.AllDifferent;

public class QueensVisual {

	public static void main(String[] args) {
		
		//========= Problem Representation ==================
		ProblemVisual problem = new ProblemVisual("Queens");
		String arg = (args.length < 1) ? "4" : args[0];
		int size = Integer.parseInt(arg);
		problem.log("Queens " + size + ". ");
		String vizFilename = "QueensProblem.viz";
		problem.startVisualization(vizFilename);

		// create 3 arrays of variables
		Var[] x = problem.varArray("x",1, size, size);
		Var[] x1 = new Var[size];
		Var[] x2 = new Var[size];		
		for (int i = 0; i < size; i++) {
			x1[i] = x[i].add(i);
			x2[i] = x[i].sub(i);
		}
		problem.register(x);

		// post "all different" constraints
		new AllDifferentVisual(x).post();
		new AllDifferent(x1).post();
		new AllDifferent(x2).post();
		
		problem.snapshot();
		
		//========= Problem Resolution ==================
		// Find a solution
		SolverVisual solver = new SolverVisual(problem);
		String treeFilename = "QueensProblem.tree";
		solver.startVisualization(treeFilename);
		solver.setTimeLimit(600000); // milliseconds
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(x);
		strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_MIN_VALUE); 
		strategy.setValueSelectorType(ValueSelectorType.MIN);
		Solution solution = solver.findSolution();
		if(solution == null)
			problem.log("no solutions found");
		else{
			solution.log();
		}
		solver.stopVisualization();
		problem.stopVisualization();
		solver.logStats();
		// Create SVG file
		String configFilename = "configuration.xml";
		try {
			Viz.runViz(configFilename, treeFilename, vizFilename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

