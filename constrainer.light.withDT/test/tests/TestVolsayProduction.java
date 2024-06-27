package tests;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalFastMinimize;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

public class TestVolsayProduction {

	public static void main(String[] args) {

		Constrainer C = new Constrainer("test GoalMinimize");
		try {
			// VolsayProduction
			IntVar x = C.addIntVar(0, 500, "gas", IntVar.DOMAIN_PLAIN);
			IntVar y = C.addIntVar(0, 500, "chloride", IntVar.DOMAIN_PLAIN);
			IntExpArray yx = new IntExpArray(C, x, y);

			C.postConstraint(x.add(y).le(50));
			C.postConstraint(x.mul(3).add(y.mul(4)).le(180));
			C.postConstraint(y.le(40));
			IntExp cost = (x.mul(40).add(y.mul(50))).neg();

			Goal minimize = new GoalFastMinimize(new GoalGenerate(yx), cost);
			boolean flag = C.execute(minimize);
			
		} catch (Failure f) {
			f.printStackTrace();
		}
	}

}