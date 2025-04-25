package javax.constraints.linear;

import javax.constraints.impl.LinearSolver;
import javax.constraints.impl.Problem;

public class LinearSolverFactory {

	public static LinearSolver newLinearSolver(Problem problem) {
	    String solverClass = "javax.constraints.linear.impl.LinearSolver";
	    //String solverClass = "javax.constraints.linear.LinearSolver";
		try {
			LinearSolver solver = (LinearSolver) Class.forName(solverClass).newInstance();
			solver.setProblem(problem);
			problem.setSolver(solver);
			return solver;
		} catch (Exception e) {
			throw new RuntimeException(
					"LinearSolverFactory: Can not create an instance of the class " + solverClass,e);
		}
	}

}
