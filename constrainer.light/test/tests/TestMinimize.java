package tests;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalFastMinimize;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

public class TestMinimize {

	public static void main(String[] args) {

		Constrainer C = new Constrainer("test GoalMinimize");
		try {
			IntVar x = C.addIntVar(-100, 100, "x", IntVar.DOMAIN_PLAIN);
			IntVar y = C.addIntVar(-100, 100, "y", IntVar.DOMAIN_PLAIN);
			IntExpArray yx = new IntExpArray(C, x, y);

			C.postConstraint(y.ge(x.sqr()));
			C.postConstraint(y.ge(x.add(2)));
			IntExp cost = y.sub(x);
			cost.name("cost");

			Goal minimize = new GoalFastMinimize(new GoalGenerate(yx), cost);
			boolean flag = C.execute(minimize);
			System.out.println(x);
			System.out.println(y);
			System.out.println(cost);
		} catch (Exception e) {
			System.out.println("test failed");
		}
	} 

}