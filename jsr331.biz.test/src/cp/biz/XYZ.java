package cp.biz;

import javax.constraints.Constraint;
import javax.constraints.Solution;
import javax.constraints.Var;

import jsr331.biz.BizProblem;
import cp.constrainer.CSP;

public class XYZ {

	public static void main(String[] args) {

		CSP csp = new CSP("XYZ");
		BizProblem bp = new BizProblem(csp);
		csp.profileOn();

		// define CP-level variables and constraints
		Var x = csp.addVar("X", 0, 10);
		Var y = csp.addVar("Y", 0, 10);
		Var z = csp.addVar("Z", 0, 10);

		Constraint c1 = csp.add(x.lt(y)); c1.setName("X < Y");
		Constraint c2 = csp.add(x.add(y).eq(z)); c2.setName("X + Y = Z");
		Constraint c3 = csp.add(x.gt(2)); c3.setName("X > 2");
		Constraint c4 = csp.add(y.lt(8)); c4.setName("Y < 8");

		Var cost = csp.addVar("cost", 2, 25);
		csp.setObjective(cost);

		Var xy3_4Z = x.mul(3).mul(y).sub(z.mul(4));
		Constraint c5 = csp.add(cost.eq(xy3_4Z)); c5.setName("cost = 3XY - 4Z");
		Constraint c6 = csp.add(cost.le(16)); c6.setName("cost < 16");

		// update business problem
		bp.addIntegers();
		bp.addConstraints();
		bp.displayVars();
		bp.displayConstraints();
		bp.log("Post Constraints");
		bp.postConstraints();
		bp.displayVars();

		Solution solution = bp.findSolution();
		if (solution != null) {
			bp.log("Solution is found");
			solution.log();
		}
		else {
			bp.log("No solutions");
		}
		csp.profileLog();

	}

}
