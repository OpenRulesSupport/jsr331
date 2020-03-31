package javax.constraints;

public class ProblemFactory {

	public static Problem newProblem(String problemName) {
		try {
			Problem problem = (Problem) Class.forName(
					"javax.constraints.impl.Problem").newInstance();
			problem.setName(problemName);
			return problem;
		} catch (Exception e) {
			throw new RuntimeException(
					"ProblemFactory: Can not create an instance of the class javax.constraints.impl.Problem",
					e);
		}
	}

}
