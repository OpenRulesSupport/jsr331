package javax.constraints.linear;

import javax.constraints.impl.Problem;

public class LinearSolverFactory {

	public static LinearSolver newLinearSolver(Problem problem) {
		try {
			LinearSolver solver = (LinearSolver) Class.forName(
					"javax.constraints.linear.impl.LinearSolver").newInstance();
			solver.setProblem(problem);
			problem.setSolver(solver);
			return solver;
		} catch (Exception e) {
			throw new RuntimeException(
					"LinearSolverFactory: Can not create an instance of the class javax.constraints.impl.LinearSolver",
					e);
		}
	}

}
