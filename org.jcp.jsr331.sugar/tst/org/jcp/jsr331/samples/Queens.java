package org.jcp.jsr331.samples;

/* ------------------------------------------------------------
 The eight-queens problem is a well known problem that involves
 placing eight queens on a chess board in such a way that none
 of them can capture any other using the conventional moves
 allowed to a queen.  In other words, the problem is to select
 eight squares on a chess board so that any pair of selected
 squares is never aligned vertically, horizontally, nor
 diagonally.
 ------------------------------------------------------------ */
import javax.constraints.*;

public class Queens {
	
	Problem p = ProblemFactory.newProblem("Queens");

	int size;
	Var[] x;

	public Queens(int size) {
		this.size = size;
	}

	public void define() {
		p.log("Queens " + size + ". ");
		// create 3 arrays of variables
		x = p.variableArray("x",0, size-1, size);
		Var[] x1 = new Var[size];
		Var[] x2 = new Var[size];		
		for (int i = 0; i < size; i++) {
			x1[i] = x[i].plus(i);
			x2[i] = x[i].minus(i);
		}

		// post "all different" constraints
		p.postAllDifferent(x);
		p.postAllDifferent(x1);
		p.postAllDifferent(x2);
	}
	
	public void solve() {

		//========= Problem Resolution ==================
		// Find a solution
		Solver solver = p.getSolver();
		solver.setTimeLimit(600000); // milliseconds
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(x);
		strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_MIN_VALUE); 
		strategy.setValueSelectorType(ValueSelectorType.MIN);
		Solution solution = solver.findSolution();
		if(solution == null)
			p.log("no solutions found");
		else{
			solution.log();
		}
		solver.logStats();
	}
	
	public static void main(String[] args) {
		String arg = (args.length == 0) ? "8" : args[0];
		int n = Integer.parseInt(arg);
		Queens q = new Queens(n);
		q.define();
		q.solve();
	}
}

